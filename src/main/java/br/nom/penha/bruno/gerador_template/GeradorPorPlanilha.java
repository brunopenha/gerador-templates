package br.nom.penha.bruno.gerador_template;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import br.nom.penha.bruno.gerador_template.dto.ValoresPlanilha;

public class GeradorPorPlanilha {

	public void geraClasse(String pacoteParam, String classeParam, Class<?> entidadeParam, String labelParam, String destinoParam)
			throws JClassAlreadyExistsException, IOException {

		JCodeModel cm = new JCodeModel();
		// Nome da Classe
		JDefinedClass dc = cm._class(pacoteParam + ".template." + classeParam);

		// Nome dos Metodos
		JMethod m = dc.method(JMod.PUBLIC, void.class, "load");
		// Monta o tratamento do metodo
		JBlock jBlock = m.body();
		
		// Obter os campos da planilha
		String caminhoArquivo = "S1000_v2.2.csv"; // "META-INF/S1000.csv";
		List<String> campos = obtemCamposPlanilha(caminhoArquivo);
		
		List<Class<?>> classesFilhas = obtemClassesFilhas(entidadeParam, pacoteParam);

		montaTemplatePorClasse(pacoteParam, entidadeParam, labelParam, jBlock);
		
		// Criar os atributos das classes filhas		
		for (Class<?> classeFilha : classesFilhas) {
			montaTemplatePorClasse(pacoteParam, classeFilha, labelParam, jBlock);
		}
				
		geraArquivoExterno(cm, destinoParam);
	}

	private List<String> obtemCamposPlanilha(String nomeArquivo) {
		List<String> linhas = null;
		
		List<ValoresPlanilha> dadosLayout = null;
		
		//BufferedReader leitor = null;
		String dirAtual = System.getProperty("user.dir");
		///home/brunopenha/dtp/projetos/esocial-eventos-transformacao/gerador-templates/src/main/resources/S1000_v2.2.csv
		nomeArquivo = dirAtual + "/src/main/resources/" + nomeArquivo;
		System.out.println("Procurando arquivo em : "+ nomeArquivo);
				
		//leitor = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivo)));
		//String linha = null;
		try {
/*			
			linhas = Files.lines(new File(nomeArquivo).toPath())
            .map(s -> s.trim())
            .skip(2) // Pulo o cabeçalho
            .filter(s -> !s.isEmpty())
            //.forEach(System.out::println)
            .collect(Collectors.toList())
            ;
*/

			dadosLayout = Files.lines(new File(nomeArquivo).toPath())
            .map(s -> s.trim())
            .skip(2) // Pulo o cabeçalho
            .filter(s -> !s.isEmpty())
            .map(mapeiaParaItem)
            .collect(Collectors.toList())
            ;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return linhas;
	}
	
	private Function<String, ValoresPlanilha> mapeiaParaItem = (line) -> {

		  String[] p = line.split(",");// a CSV has comma separated lines

		  ValoresPlanilha item = new ValoresPlanilha();

		  item.setRegistro(Integer.parseInt(p[0]));//<-- this is the first column in the csv file

		  if (p[1] != null && p[1].trim().length() > 0) {
			  item.setCampo(p[1]);
		  }
		  
		  if (p[2] != null && p[2].trim().length() > 0) {
			  item.setPai(p[2]);
		  }

		  if (p[3] != null && p[3].trim().length() > 0) {
			  item.setEle(p[3]);
		  }
		  
		  if (p[4] != null && p[4].trim().length() > 0) {
			  item.setTipo(p[4]);
		  }
		  
		  if (p[5] != null && p[5].trim().length() > 0) {
			  item.setOcorrencia(p[5]);
		  }
		  
		  if (p[6] != null && p[6].trim().length() > 0) {
			  item.setTamanho(Integer.parseInt(p[6]));
		  }
		  
		  if ((p[7] != null && p[7].trim().length() > 0) && (!p[7].trim().equals("-"))) {
			  item.setDecimal(Integer.parseInt(p[7]));
		  }
		  
		  if (p[8] != null && p[8].trim().length() > 0) {
			  item.setDescricao(p[8]);
		  }

		  return item;

		};

	/**
	 * @param pacoteParam
	 * @param entidadeParam
	 * @param labelParam
	 * @param jBlock
	 */
	private void montaTemplatePorClasse(String pacoteParam, Class<?> entidadeParam, String labelParam, JBlock jBlock) {
		jBlock.directStatement("Fixture.of(" + entidadeParam.getSimpleName() + ".class).addTemplate(\"" + labelParam + "\", new Rule(){{");
		List<String> linhasAdicionadas = preencheLinhas(entidadeParam, pacoteParam, labelParam);
		
		for (String linha : linhasAdicionadas) {
			jBlock.directStatement(linha);
		}
		
		jBlock.directStatement("}});");
	}


	private List<String> preencheLinhas(Class<?> entidadeParam, String pacoteParam, String labelParam) {
		
		List<String> retorno = new ArrayList<String>(); 
		String linha = null;
		
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(entidadeParam);
//			System.out.println("\n\n\t******" + info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
			String tipo = propertyDescriptor.getPropertyType().toString(); 
			if(tipo.contains(pacoteParam)){
				obtemAtributos(propertyDescriptor.getPropertyType(),pacoteParam);
				linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", one("+ propertyDescriptor.getPropertyType().getSimpleName() + ".class, \"" + labelParam +"\"));";
			}else{
				if(propertyDescriptor.getPropertyType().getSimpleName().contains("String")){
					linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", \"valor\");";
				}else if(propertyDescriptor.getPropertyType().getSimpleName().equals("Class")){
					linha = " ";
				}else { // if(!propertyDescriptor.getDisplayName().equals("class")){
					linha = " add(\""+ propertyDescriptor.getDisplayName() + "\", ("+ propertyDescriptor.getPropertyType().getSimpleName() + ") " + formataTipoAtributo(propertyDescriptor,entidadeParam,labelParam) + ");";	
				}
				
			}
			
			retorno.add(linha);
			
		}

		return retorno;

	}

	/**
	 * @param propertyDescriptor
	 * @return
	 */
	private String formataTipoAtributo(PropertyDescriptor propertyDescriptor, Class<?> entidadeParam, String labelParam) {
		
		String retorno = "\"valor\"";
		
		Class<?> tipo = propertyDescriptor.getPropertyType();
		
		if(tipo.isAssignableFrom(Calendar.class)){
			retorno = "instant(\"18 years ago\")";
		}else if(tipo.isAssignableFrom(Long.class)){
			retorno = "random(Long.class, range(1L, 200L))";
		}else if(tipo.isAssignableFrom(int.class)){
			retorno = "(int) 1";
		}else if(tipo.isAssignableFrom(byte.class)){
			retorno = "(byte) 1";
		}else if(tipo.isAssignableFrom(XMLGregorianCalendar.class)){
			retorno = "TransformacaoTesteUtil.criarDataParaXML(\"01-03-2017\")" ;
		}else if(tipo.isAssignableFrom(BigInteger.class)){
			retorno = "new BigInteger(\"15\")" ;
		}else if(tipo.isAssignableFrom(List.class)){
			
			//Pegar o tipo da lista
			Field campoLista = null;
			try {
				campoLista = entidadeParam.getDeclaredField(propertyDescriptor.getName());
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
	        ParameterizedType tipoLista = (ParameterizedType) campoLista.getGenericType();
	        Class<?> classeLista = (Class<?>) tipoLista.getActualTypeArguments()[0];
			
			retorno = "has(3).of("+ classeLista.getSimpleName() + ".class, \"" + labelParam + "\", \"" + labelParam + "\", \"" + labelParam + "\")" ;
		}
		
		return retorno;
	}
	
	private List<Class<?>> obtemClassesFilhas(Class<?> entidade, String pacote) {
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(entidade);
//			System.out.println("\n\n\t******" + info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
			String tipo = propertyDescriptor.getPropertyType().toString(); 

			if(tipo.contains(pacote)){
				classes.add(propertyDescriptor.getPropertyType());
				List<Class<?>> classesFilhas = obtemClassesFilhas(propertyDescriptor.getPropertyType(),pacote);
				classes.addAll(classesFilhas);
			}
			
		}

		return classes;

	}

	private PropertyDescriptor[] obtemAtributos(Class<?> entidade, String pacote) {
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(entidade);
//			System.out.println("\n\n\t******" + info.getBeanDescriptor());
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		for (PropertyDescriptor propertyDescriptor : pds) {
			
//			System.out.println("\nObtendo atributos da classe " + entidade.getSimpleName());
			
			String tipo = propertyDescriptor.getPropertyType().toString(); 
			if(tipo.contains(pacote)){
				obtemAtributos(propertyDescriptor.getPropertyType(),pacote);
			}
			
		}

		return pds;
	}

	/**
	 * @param cm
	 * @throws IOException
	 */
	private void geraArquivoExterno(JCodeModel cm, String destino) throws IOException {
		File file = new File(destino);
		file.mkdirs();
		cm.build(file);
	}
}
