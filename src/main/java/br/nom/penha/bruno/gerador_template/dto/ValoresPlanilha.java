package br.nom.penha.bruno.gerador_template.dto;

public class ValoresPlanilha {

	private Integer registro;
	private String campo;
	private String pai;
	private String ele;
	private String tipo;
	private String ocorrencia;
	private Integer tamanho;
	private Integer decimal;
	private String descricao;
	
	
	public Integer getRegistro() {
		return registro;
	}
	public void setRegistro(Integer registro) {
		this.registro = registro;
	}
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public String getPai() {
		return pai;
	}
	public void setPai(String pai) {
		this.pai = pai;
	}
	public String getEle() {
		return ele;
	}
	public void setEle(String ele) {
		this.ele = ele;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
	public Integer getTamanho() {
		return tamanho;
	}
	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}
	public Integer getDecimal() {
		return decimal;
	}
	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
