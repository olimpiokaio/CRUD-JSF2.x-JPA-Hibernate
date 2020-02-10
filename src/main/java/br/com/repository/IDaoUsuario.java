package br.com.repository;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.model.Usuario;

public interface IDaoUsuario {
	
	Usuario consultarUsuario(String login, String senha);
	
	/* SelectItem é usado para lista de items carregados geralmente em combobox */
	List<SelectItem> listaEstados();
}
