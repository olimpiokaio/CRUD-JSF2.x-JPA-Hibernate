package br.com.repository;

import br.com.model.Usuario;
import java.util.List;
import br.com.model.Lancamento;

public interface IDaoLancamentos {
	public List<Lancamento> getLancamentos(Usuario usuario);
}
