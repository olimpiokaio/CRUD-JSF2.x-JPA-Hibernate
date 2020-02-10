package br.com.controler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.dao.DaoGeneric;
import br.com.model.Lancamento;
import br.com.model.Usuario;
import br.com.repository.IDaoUsuairoLancamentos;

@ViewScoped
@ManagedBean(name = "lancamentoBean")
public class LancamentoBean {

	private Lancamento lancamento = new Lancamento();
	private DaoGeneric<Lancamento> daoGeneric = new DaoGeneric<Lancamento>();
	private IDaoUsuairoLancamentos idLancamentos = new IDaoUsuairoLancamentos();
	private List<Lancamento> listaLancamentos;
	

	/* ------------------------ gettes and settes ---------------------------- */
	
	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public DaoGeneric<Lancamento> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Lancamento> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Lancamento> getListaLancamentos() {
		return listaLancamentos;
	}

	public void setListaLancamentos(List<Lancamento> listaLancamentos) {
		this.listaLancamentos = listaLancamentos;
	}
	
	/* ------------------------ metodos especiais ---------------------------- */
	
	public void salvar() {
		// recupera o usuario logado
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Usuario usuairoSessao = (Usuario) externalContext.getSessionMap().get("usuarioLogado");
		
		// atribui os lancamentos ao usuario logado no sistema
		this.lancamento.setUsuario(usuairoSessao);
		// salva o lancamento
		daoGeneric.salvarEntidade(this.lancamento);
		
		// atualizar lista de lancamentos
		this.getListar();

		// limpa campos 
		this.lancamento = new Lancamento();
		
	}
	
	public void novoLancamento() {
		this.lancamento = new Lancamento();
	}
	
	@PostConstruct
	public void getListar() {
		// recuperar o usuario da sessão
		FacesContext fc = FacesContext.getCurrentInstance();
		Usuario user = (Usuario) fc.getExternalContext().getSessionMap().get("usuarioLogado");
		
		// busca lancamentos do usuario logado na sessão
		listaLancamentos = idLancamentos.getLancamentos(user);
	}
	
	public void getEditar() {
		lancamento = daoGeneric.bucarEntidade(Lancamento.class, lancamento.getId());
	}
	
	public void deletar() {
		daoGeneric.deletarEntidade(Lancamento.class, this.lancamento.getId());
		// atualizar lista de lancamentos
		this.getListar();
		this.lancamento = new Lancamento();
	}
	
}

















