package br.univel;

import br.univel.anotacoes.Coluna;

public class Cliente {
	
	@Coluna(pk=true, nome="idclifor")
	private int id;
	@Coluna(nome = "nomecliente", tamanho = 150 )
	private String nome;
	@Coluna(nome = "endereco", tamanho = 200)
	private String endereco;
	@Coluna(nome = "fone", tamanho = 10)
	private String telefone;
	@Coluna(nome = "estCivil")
	private boolean estadocivil;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public boolean isEstadocivil() {
		return estadocivil;
	}

	public void setEstadocivil(boolean estadocivil) {
		this.estadocivil = estadocivil;
	}

	public Cliente(int id, String nome, String endereco, String telefone, boolean estadocivil) {
		super();
		this.id = id;
		this.nome = nome;
		this.endereco = endereco;
		this.telefone = telefone;
		this.estadocivil = estadocivil;
	}

	
}