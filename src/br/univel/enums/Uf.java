package br.univel.enums;

import java.math.BigDecimal;

public enum Uf {

	PR("Paraná", "Sul") {
		@Override
		public BigDecimal getValorImposto(BigDecimal valor) {
			return valor.multiply(new BigDecimal(2.2f));
		}
	},

	SP("São Paulo", "Sudeste") {
		@Override
		public BigDecimal getValorImposto(BigDecimal valor) {
			return valor.multiply(new BigDecimal(2.3f));
		}
	},

	MT("Mato Grosso", "Centro-Oeste") {
		@Override
		public BigDecimal getValorImposto(BigDecimal valor) {
			return valor.multiply(new BigDecimal(1.2f));
		}
	};

	private final String nome;
	private final String regiao;

	private Uf(String nome, String regiao) {
		this.nome = nome;
		this.regiao = regiao;
	}

	public String getNome() {
		return nome;
	}

	public String getRegiao() {
		return regiao;
	}

	public static final Uf getPorNome(String nome) {
		for (Uf uf : values()) {
			if (uf.getNome().equals(nome)) {
				return uf;
			}
		}
		throw new RuntimeException("Nome não encontrado: " + nome);
	}

	public abstract BigDecimal getValorImposto(BigDecimal valor);

}
