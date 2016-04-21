package br.univel.enums;

public enum Mes {

	JANEIRO("Janeiro", 31),
	FEVEREIRO("Fevereiro", 29),
	MARCO("Março", 31),
	ABRIL("Abril", 30),
	MAIO("Maio", 31),
	JUNHO("Junho", 30),
	JULHO("Julho", 31),
	AGOSTO("Agosto", 31),
	SETEMBRO("Setembro", 30),
	OUTUBRO("Outubro", 31),
	NOVEMBRO("Novembro", 30),
	DEZEMBRO("Dezembro", 31);

	private final String nome;
	private final int dias;

	private Mes(String nome, int dias) {
		this.nome = nome;
		this.dias = dias;
	}

	public String getNome() {
		return nome;
	}

	public int getDias() {
		return dias;
	}

	@Override
	public String toString() {
		return getNome();
	}

}
