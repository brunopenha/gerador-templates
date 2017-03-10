package br.nom.penha.bruno.gerador_template;

import java.io.IOException;

import com.sun.codemodel.JClassAlreadyExistsException;

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
		GeradorPorEntidades geradorPorEntidade = new GeradorPorEntidades();
		GeradorPorPlanilha geradorPorPlanilha = new GeradorPorPlanilha();

		try {
			
			String pacote = "br.nom.penha.bruno";
			 
			Class<?> entidadeMapeada = Client.class;
			String nomeTemplate = entidadeMapeada.getSimpleName() + "Template";
			String label = "valido";
			String destino = "./target/gerado";
			
			//geradorPorEntidade.geraClasse(pacote, nomeTemplate, entidadeMapeada, label, destino);
			geradorPorPlanilha.geraClasse(pacote, nomeTemplate, entidadeMapeada, label, destino);
			
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	

}
