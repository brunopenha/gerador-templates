package br.nom.penha.bruno.gerador_template.entidade;

import java.io.Serializable;

public class Neighborhood implements Serializable {
	
	private static final long serialVersionUID = -8218786776801074885L;
	
	private String name;
	
	public Neighborhood(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
