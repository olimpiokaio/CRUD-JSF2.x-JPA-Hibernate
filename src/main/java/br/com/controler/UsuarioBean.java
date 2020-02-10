package br.com.controler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.jpautil.JPAUtil;
import br.com.model.Cidades;
import br.com.model.Estados;
import br.com.model.Usuario;
import br.com.repository.IDaoUsuarioImpl;

@ManagedBean(name="usuariobean")
@ViewScoped
public class UsuarioBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Usuario usuario = new Usuario();
	
	private DaoGeneric<Usuario> daoGeneric = new DaoGeneric<>();
	
	private List<Usuario> lista = new ArrayList<Usuario>();
	
	private IDaoUsuarioImpl idaoUsuarioImpl = new IDaoUsuarioImpl();
	
	private List<Estados> estados;
	
	private List<Cidades> cidades;
	
	// informa se o usuario logado tem acesso de adiministrador para deletar usuarios
	private boolean permissao;

	/* ----------------------------- GETs e SETs ---------------------------- */
	
	public List<Cidades> getCidades() {
		return cidades;
	}
	
	public void setCidades(List<Cidades> cidades) {
		this.cidades = cidades;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public List<Usuario> getLista() {
		return lista;
	}
	
	public void setList(List<Usuario> lista) {
		this.lista = lista;
	}
	
	public boolean getPermissao() {
		// recuperando sessão usuarioLogado
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Usuario usuarioSessao = (Usuario) externalContext.getSessionMap().get("usuarioLogado");
		
		this.setPermissao(usuarioSessao.getPerfil().equals("ADIMINISTRADOR"));

		return permissao;
	}

	public void setPermissao(boolean permissao) {
		this.permissao = permissao;
	}
	
	public List<Estados> getEstados() {
		return estados;
	}
	
	public void setEstados(List<Estados> estados) {
		this.estados = estados;
	}

	/*-------------------------------------------------------------------------*/
	/*--------- METODOS ESPECIAIS ---------*/
	/*-------------------------------------------------------------------------*/
	
	public String carregaCidades(AjaxBehaviorEvent e) {
		Integer idEstado = (Integer) ((UIOutput)e.getSource()).getValue();
		this.usuario.setEstadoId(idEstado);
		
		System.out.println(idEstado + " : " + this.usuario.getEstadoId());

		if (idEstado != null && idEstado >= 0) {
			try {
				List<Cidades> res = JPAUtil.getEntityManager().
						createQuery("from Cidades where estados.id = " 
								+ idEstado).getResultList();
				this.cidades = res;
			}catch(Exception ex) {
				System.err.println(ex);
			}
		} else {
			this.cidades = null;
		}
		return "cadUsuario?faces-redirect=true";
	}
	
	public String deslogarUsuario() {
		// destroi a sessão do usuario
		FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().remove("usuarioLogado");
		
		/*obs: NÃO É NECESSARIO (usuando httpservletrequest para garantir a invalidação da sessão)*/
		HttpServletRequest httpServletRequest = (HttpServletRequest)
				FacesContext.getCurrentInstance().getExternalContext().getRequest();
		httpServletRequest.getSession().invalidate();
		
		// retorna a pagina de index para que seja feito o login novamente
		return "index";
	}
	
	public String buscaCep(AjaxBehaviorEvent event) {
		try {			
			
			URL url = new URL("https://viacep.com.br/ws/" + this.usuario.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			Usuario gson = new Gson().fromJson(jsonCep.toString(), Usuario.class);
			
			this.usuario.setCep(gson.getCep());
			this.usuario.setLogradouro(gson.getLogradouro());
			this.usuario.setComplemento(gson.getComplemento());
			this.usuario.setBairro(gson.getBairro());
			this.usuario.setLocalidade(gson.getLocalidade());
			this.usuario.setUf(gson.getUf());
			this.usuario.setUnidade(gson.getUnidade());
			this.usuario.setIbge(gson.getIbge());
			this.usuario.setGia(gson.getGia());
			
		}catch(Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("cep não encontrado"));
		}
		return "cadUsuario?faces-redirect=true";
	}
	
	public void salvar() {
		daoGeneric.salvarEntidade(usuario);
		usuario = new Usuario();
		this.listar();
	}
	
	@PostConstruct
	private void listar() {
		// carrega tabela de Usuario
		lista = daoGeneric.listarEntitdade(Usuario.class);
		
		// carrega comboBox de Estados
		this.estados = idaoUsuarioImpl.getListaEstados();
	}
	
	public void atualizar() {
		usuario = daoGeneric.atualizarEntidade(usuario);
		mostrarMsg("Salvo com sucesso!");
		this.listar();
	}
	
	public void novoCadastro() {
		usuario = new Usuario();
		this.listar();
	}
	
	public void deletar() {
		Long idDeleta = usuario.getId();
		daoGeneric.deletarEntidade(Usuario.class, idDeleta);
		usuario = new Usuario();
		mostrarMsg("Deletado com sucesso!");
		this.listar();
	}
	
	public String autenticaLogin() {
		Usuario usuarioSessao = idaoUsuarioImpl.consultarUsuario(this.usuario.getLogin(), this.usuario.getSenha());
		if(usuarioSessao != null) { // retorna o usuário
			// adiciona o usuarioLogado na sessão
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", usuarioSessao);
			return "cadUsuario?faces-redirect=true";
		}
		return "index?faces-redirect=true";
	}
	
	public void mostrarMsg(String msg) {
		FacesContext fcotext = FacesContext.getCurrentInstance();
		FacesMessage fmessage = new FacesMessage(msg);
		fcotext.addMessage(null, fmessage);
	}
	
}




/*
 * 
 * <!--  ESTADOS STOP
				<h:outputLabel value="ESTADOS:*"/>
				<h:selectOneMenu valeu="#{usuariobean.usuario.idEstado}" immediate="true">
					<f:selectItem itemLabel="..[SELECIONE].." noSelectionOption="true" />
					<f:selectItems value="#{usuariobean.estados}" itemLabel="#{usuariobean.estados.nome}"/>
					<f:ajax event="change" execute="@this" listener="#{usuariobean.carregaCidades}" 
							render="@form"/>
				</h:selectOneMenu> ESTADO STOP-->*/















