package br.nom.penha.bruno.gerador_template;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import br.nom.penha.bruno.gerador_template.entidade.Client;

/**
 * Hello world!
 *
 */
public class App {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		App app = new App();

		try {
			
			String pacote = "br.nom.penha.bruno";
			String nomeTemplate = "EntidadeTemplate"; 
			Class<?> entidadeMapeada = Client.class;
			String label = "valido";
			
			
			app.geraClasse(pacote, nomeTemplate, entidadeMapeada, label);
		} catch (JClassAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void geraClasse(String pacoteParam, String classeParam, Class<?> entidadeParam, String labelParam)
			throws JClassAlreadyExistsException, IOException {

		JCodeModel cm = new JCodeModel();
		// Nome da Classe
		JDefinedClass dc = cm._class(pacoteParam + ".template." + classeParam);

		// Nome e tipo dos atributos
		// JFieldVar atributo = dc.field(mods, type, name)

		// Nome dos Metodos
		JMethod m = dc.method(JMod.PUBLIC, void.class, "load");
		// Monta o tratamento do metodo
		JBlock jBlock = m.body();
		
		//Obter os atributos da classe
		List<Class<?>> classesFilhas = obtemClassesFilhas(entidadeParam, pacoteParam);

		montaTemplatePorClasse(pacoteParam, entidadeParam, labelParam, jBlock);
		// Criar os atributos das classes filhas		
		for (Class<?> classeFilha : classesFilhas) {
			montaTemplatePorClasse(pacoteParam, classeFilha, labelParam, jBlock);
		}
				
		//geraArquivoExterno(cm);
	}

	/**
	 * @param pacoteParam
	 * @param entidadeParam
	 * @param labelParam
	 * @param jBlock
	 */
	private void montaTemplatePorClasse(String pacoteParam, Class<?> entidadeParam, String labelParam, JBlock jBlock) {
		//jBlock.directStatement("Fixture.of(RubricaValidade.class).addTemplate(\"valido\", new Rule(){{");
		jBlock.directStatement("Fixture.of(" + entidadeParam.getSimpleName() + ".class).addTemplate(\"" + labelParam + "\", new Rule(){{");
		// For para cada atributo da entidade
		//PropertyDescriptor[] pds = obtemAtributos(entidadeParam, pacoteParam);
		
		List<String> linhasAdicionadas = preencheLinhas(entidadeParam, pacoteParam, labelParam);
		
		for (String linha : linhasAdicionadas) {
			jBlock.directStatement(linha);
		}
		
//		for (PropertyDescriptor propertyDescriptor : pds) {
//			
//			final String linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", "+ propertyDescriptor.getPropertyType().getSimpleName() + ".class, \"valor\");";
//			System.out.println(linha);
//			jBlock.directStatement(linha);
//			
//			
//		}
		jBlock.directStatement("}});");
	}


	public List<String> preencheLinhas(Class<?> entidadeParam, String pacoteParam, String labelParam) {
		
		List<String> retorno = new ArrayList<String>(); 
		String linha = null;
		
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(entidadeParam);
			System.out.println("\n\n\t******" + info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
			System.out.println("\nObtendo atributos da classe " + entidadeParam.getSimpleName());
			System.out.println("propertyDescriptor.getDisplayName -> " +  propertyDescriptor.getDisplayName());
			System.out.println("propertyDescriptor.getName -> " +  propertyDescriptor.getName());
			System.out.println("propertyDescriptor.getShortDescription -> " +  propertyDescriptor.getShortDescription());
			System.out.println("propertyDescriptor.getClass -> " +  propertyDescriptor.getClass());
			System.out.println("propertyDescriptor.getPropertyEditorClass -> " +  propertyDescriptor.getPropertyEditorClass());
			System.out.println("propertyDescriptor.getPropertyType -> " +  propertyDescriptor.getPropertyType());
			System.out.println("propertyDescriptor.getReadMethod -> " +  propertyDescriptor.getReadMethod());
			System.out.println("propertyDescriptor.getWriteMethod -> " +  propertyDescriptor.getWriteMethod());
			
			String tipo = propertyDescriptor.getPropertyType().toString(); 
			//if(tipo.contains("br.nom.penha.bruno")){
			if(tipo.contains(pacoteParam)){
				PropertyDescriptor[] pd = obtemAtributos(propertyDescriptor.getPropertyType(),pacoteParam);
				//add("trabEstrangeiro", one(TTrabEstrang.class, "TTrabEstrangValido"));
				linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", one("+ propertyDescriptor.getPropertyType().getSimpleName() + ".class, \"" + propertyDescriptor.getPropertyType().getSimpleName() + labelParam.toUpperCase() +"\"));";
			}else{
				//add("sexo", "M");
				//add("racaCor", (byte)3 );
				if(propertyDescriptor.getPropertyType().getSimpleName().contains("String")){
					linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", \"valor\");";
				}else{
					linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", ("+ propertyDescriptor.getPropertyType() + ") \"valor\");";	
				}
				
			}
			
			retorno.add(linha);
			
		}

		return retorno;
		// System.out.println("Atributos:\n\n");

	}
	
	public List<Class<?>> obtemClassesFilhas(Class<?> entidade, String pacote) {
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(entidade);
			System.out.println("\n\n\t******" + info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
			System.out.println("\nObtendo atributos da classe " + entidade.getSimpleName());
			System.out.println("propertyDescriptor.getDisplayName -> " +  propertyDescriptor.getDisplayName());
			System.out.println("propertyDescriptor.getName -> " +  propertyDescriptor.getName());
			System.out.println("propertyDescriptor.getShortDescription -> " +  propertyDescriptor.getShortDescription());
			System.out.println("propertyDescriptor.getClass -> " +  propertyDescriptor.getClass());
			System.out.println("propertyDescriptor.getPropertyEditorClass -> " +  propertyDescriptor.getPropertyEditorClass());
			System.out.println("propertyDescriptor.getPropertyType -> " +  propertyDescriptor.getPropertyType());
			System.out.println("propertyDescriptor.getReadMethod -> " +  propertyDescriptor.getReadMethod());
			System.out.println("propertyDescriptor.getWriteMethod -> " +  propertyDescriptor.getWriteMethod());
			
			String tipo = propertyDescriptor.getPropertyType().toString(); 
			//if(tipo.contains("br.nom.penha.bruno")){
			if(tipo.contains(pacote)){
				classes.add(propertyDescriptor.getPropertyType());
				List<Class<?>> classesFilhas = obtemClassesFilhas(propertyDescriptor.getPropertyType(),pacote);
				classes.addAll(classesFilhas);
			}
			
		}

		return classes;
		// System.out.println("Atributos:\n\n");

	}

	public PropertyDescriptor[] obtemAtributos(Class<?> entidade, String pacote) {
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(entidade);
			System.out.println("\n\n\t******" + info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
			System.out.println("\nObtendo atributos da classe " + entidade.getSimpleName());
			System.out.println("propertyDescriptor.getDisplayName -> " +  propertyDescriptor.getDisplayName());
			System.out.println("propertyDescriptor.getName -> " +  propertyDescriptor.getName());
			System.out.println("propertyDescriptor.getShortDescription -> " +  propertyDescriptor.getShortDescription());
			System.out.println("propertyDescriptor.getClass -> " +  propertyDescriptor.getClass());
			System.out.println("propertyDescriptor.getPropertyEditorClass -> " +  propertyDescriptor.getPropertyEditorClass());
			System.out.println("propertyDescriptor.getPropertyType -> " +  propertyDescriptor.getPropertyType());
			System.out.println("propertyDescriptor.getReadMethod -> " +  propertyDescriptor.getReadMethod());
			System.out.println("propertyDescriptor.getWriteMethod -> " +  propertyDescriptor.getWriteMethod());
			
			String tipo = propertyDescriptor.getPropertyType().toString(); 
			//if(tipo.contains("br.nom.penha.bruno")){
			if(tipo.contains(pacote)){
				PropertyDescriptor[] pd = obtemAtributos(propertyDescriptor.getPropertyType(),pacote);
			}
			
		}

		return pds;
		// System.out.println("Atributos:\n\n");

	}

	public String[] obtemNomesAtributos(Class<?> entidade) {
		
		String[] retorno = null;
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(Client.class);
			System.out.println(info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
		}

		return retorno;

	}

	/**
	 * @param cm
	 * @throws IOException
	 */
	private void geraArquivoExterno(JCodeModel cm) throws IOException {
		File file = new File("./target/gerado");
		file.mkdirs();
		cm.build(file);
	}
}
