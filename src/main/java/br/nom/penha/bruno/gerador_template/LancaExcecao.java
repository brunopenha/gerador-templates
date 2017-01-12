package br.nom.penha.bruno.gerador_template;


public class LancaExcecao {

	public static void main(String[] args) {
		
		try {
			String teste ="Dentro do Try Catch";
			throw new Exception("Dentro do Try Catch");
		}catch (Exception e){
			System.out.println("Dentro do Catch");
		} finally {
			throw new NullPointerException("Dentro do Finally");
		}
	}
}
