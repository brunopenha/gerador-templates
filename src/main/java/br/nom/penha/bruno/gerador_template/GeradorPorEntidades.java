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
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class GeradorPorEntidades {

	public void geraClasse(String pacoteParam, String classeParam, Class<?> entidadeParam, String labelParam, String destinoParam)
			throws JClassAlreadyExistsException, IOException {

		JCodeModel cm = new JCodeModel();
		// Nome da Classe
		JDefinedClass dc = cm._class(pacoteParam + ".template." + classeParam);

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
				
		geraArquivoExterno(cm, destinoParam);
	}

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
	
	private void validaXML(){
	
		
	}
}
