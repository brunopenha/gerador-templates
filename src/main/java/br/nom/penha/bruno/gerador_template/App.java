package br.nom.penha.bruno.gerador_template;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.nom.penha.bruno.gerador_template.entidade.Client;

/**
 * Hello world!
 *
 */
public class App {
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		App app = new App();

		try {
			app.geraClasse("br.gov.dataprev.projeto.template", "EntidadeTemplate", Client.class);
		} catch (JClassAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void geraClasse(String pacote, String classe, Class<?> entidade)
			throws JClassAlreadyExistsException, IOException {

		JCodeModel cm = new JCodeModel();
		// Nome da Classe
		JDefinedClass dc = cm._class(pacote + "." + classe);

		// Nome e tipo dos atributos
		// JFieldVar atributo = dc.field(mods, type, name)

		// Nome dos Metodos
		JMethod m = dc.method(JMod.PUBLIC, void.class, "load");
		// Monta o tratamento do metodo
		// m.body()._return(JExpr.lit(5));
		JBlock jBlock = m.body();
		jBlock.directStatement("Fixture.of(Client.class).addTemplate(\"valid\", new Rule(){{");
		// For para cada atributo da entidade
		// jBlock.directStatement(" add(\"id\", random(Long.class, range(1L,
		// 200L)));");
		PropertyDescriptor[] pds = obtemAtributos(Client.class);
		for (PropertyDescriptor propertyDescriptor : pds) {

			jBlock.directStatement(" add(\""+ propertyDescriptor.getDisplayName() + "\", "+ propertyDescriptor.getPropertyType().getSimpleName() + ", range(1L, 200L)));");
			
			//System.out.println(propertyDescriptor.getPropertyType() + " " + );
		}
		//
		jBlock.directStatement("}});");

		File file = new File("./target/gerado");
		file.mkdirs();
		cm.build(file);
	}

	public PropertyDescriptor[] obtemAtributos(Class<?> entidade) {
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(Client.class);
			System.out.println(info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();

		return pds;
		// System.out.println("Atributos:\n\n");

	}

	public void criaTemplate() {

		try {
			PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
			writer.println("The first line");
			writer.println("The second line");
			writer.close();
		} catch (IOException e) {
			// do something
		}

		// Aqui deve ser incluido os atributos
		List<String> lines = Arrays.asList("The first line", "The second line");
		Path file = Paths.get("the-file-name.txt");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
