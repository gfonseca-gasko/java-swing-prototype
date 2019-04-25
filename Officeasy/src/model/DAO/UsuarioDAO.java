package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Funcionario;
import model.Permissoes;
import util.Conexao;
import util.MySQL;

public class UsuarioDAO extends MySQL {

	public static boolean buscarPorLoginSenha(String login, String senha) {

		try {

			String sql = "select * from usuario where nomeUsu = ? and senha = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setString(1, login);
			comando.setString(2, senha);

			ResultSet rs = comando.executeQuery();

			if (rs.next()) {

				rs.close();
				comando.close();
				con.close();

				return true;

			}

			rs.close();
			comando.close();
			con.close();

		} catch (SQLException e) {

			JOptionPane.showMessageDialog(null, e.getMessage());

		}

		return false;

	}

	public static void alterarSenha(String nomeUsu, String senha) {

		try {

			String sql = "UPDATE usuario SET senha = ? WHERE nomeUsu = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setString(1, senha);
			comando.setString(2, nomeUsu);

			comando.executeUpdate();

			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	public static void alterarStatus(String nomeUsu, boolean status) {

		try {

			String sql = "update usuario set logado = ? WHERE nomeUsu = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setBoolean(1, status);
			comando.setString(2, nomeUsu);

			comando.executeUpdate();

			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	public static void manterLogado(String nomeUsu, boolean manterLogado) {

		try {

			String sql = "UPDATE usuario SET manterLogado = ? where nomeUsu = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setBoolean(1, manterLogado);
			comando.setString(2, nomeUsu);

			comando.executeUpdate();

			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	public static void inserirUsuario(String login, String senha, Permissoes nivel, int matricula) {

		try {

			String sql = "INSERT INTO usuario values (?,?,?,?,?,?)";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setString(1, login);
			comando.setString(2, senha);
			comando.setBoolean(3, true);
			comando.setBoolean(4, false);
			comando.setBoolean(5, false);
			comando.setInt(6, nivel.getId());

			comando.executeUpdate();
			
			comando.close();
			con.close();

			FuncionarioDAO.inserirFKUsuario(matricula, login);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, e.getMessage());
			System.out.println(e.getMessage());
		}

		JOptionPane.showMessageDialog(null, MySQL.getMSG());

	}

	public static Funcionario selecionarUsuario(String login) {

		Permissoes perm;
		Funcionario usu = null;

		try {

			String sql = "select * from usuario where nomeUsu = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setString(1, login);

			ResultSet rs = comando.executeQuery();

			if (rs.next()) {

				perm = PermissoesDAO.selecionar(rs.getInt("FKNivel"));

				usu = new Funcionario(rs.getString("nomeUsu"), rs.getString("senha"), perm, rs.getBoolean("status"),
						rs.getBoolean("manterLogado"), rs.getBoolean("logado"));

			}

			rs.close();
			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			JOptionPane.showMessageDialog(null, "Usu�rio n�o encontrado!");
		}

		return usu;
	}

	public static Funcionario buscarUsuarioPorMatricula(int matricula) {

		Permissoes perm;
		Funcionario usu = null;

		try {

			String sql = "select * from usuario where usuario.nomeUsu = funcionario.FKUsuario and funcionario.matricula = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setInt(1, matricula);

			ResultSet rs = comando.executeQuery();

			if (rs.next()) {

				perm = PermissoesDAO.selecionar(rs.getInt("FKNivel"));

				usu = new Funcionario(rs.getString("nomeUsu"), rs.getString("senha"), perm, rs.getBoolean("status"),
						rs.getBoolean("manterLogado"), rs.getBoolean("logado"));

			}

			rs.close();
			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			JOptionPane.showMessageDialog(null, "Usu�rio n�o encontrado!");
		}

		return usu;

	}

	public static List<Funcionario> listarUsuariosAtivos() {

		List<Funcionario> listaUsu = new ArrayList<Funcionario>();

		Permissoes perm;
		Funcionario usu = null;

		try {

			String sql = "select * from usuario where status = true";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			ResultSet rs = comando.executeQuery();

			while (rs.next()) {

				if (rs.next()) {
					perm = PermissoesDAO.selecionar(rs.getInt("FKNivel"));
					usu = new Funcionario(rs.getString("nomeUsu"), rs.getString("senha"), perm, rs.getBoolean("status"),
							rs.getBoolean("manterLogado"), rs.getBoolean("logado"));

					listaUsu.add(usu);
				}

			}

			rs.close();
			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return listaUsu;
	}

	public static List<Funcionario> listarUsuariosInativos() {

		List<Funcionario> listaUsu = new ArrayList<Funcionario>();

		Permissoes perm;
		Funcionario usu = null;

		try {

			String sql = "select * from usuario where status = false";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			ResultSet rs = comando.executeQuery();

			while (rs.next()) {

				if (rs.next()) {
					perm = PermissoesDAO.selecionar(rs.getInt("FKNivel"));
					usu = new Funcionario(rs.getString("nomeUsu"), rs.getString("senha"), perm, rs.getBoolean("status"),
							rs.getBoolean("manterLogado"), rs.getBoolean("logado"));

					listaUsu.add(usu);
				}

			}

			rs.close();
			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return listaUsu;
	}

	public static List<Funcionario> buscarUsuariosPorMatricula(int matricula) {

		List<Funcionario> listaUsu = new ArrayList<Funcionario>();

		Permissoes perm;
		Funcionario usu = null;

		try {

			String sql = "select usuario.nomeUsu, usuario.status, usuario.logado, usuario.FKNivel from usuario, funcionario where funcionario.matricula = ? and funcionario.FKUsuario = usuario.nomeUsu";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setInt(1, matricula);

			ResultSet rs = comando.executeQuery();

			while (rs.next()) {

				if (rs.next()) {
					perm = PermissoesDAO.selecionar(rs.getInt("FKNivel"));
					usu = new Funcionario(rs.getString("nomeUsu"), rs.getString(""), perm, rs.getBoolean("status"),
							rs.getBoolean("manterLogado"), rs.getBoolean("logado"));

					listaUsu.add(usu);
				}

			}

			rs.close();
			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return listaUsu;
	}

	public static List<Funcionario> buscarUsuariosPorLogin(String login) {

		List<Funcionario> listaUsu = new ArrayList<Funcionario>();

		Permissoes perm;
		Funcionario usu = null;

		try {

			String sql = "select usuario.nomeUsu, usuario.status, usuario.logado, usuario.FKNivel from usuario where usuario.nomeUsu = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setString(1, login);

			ResultSet rs = comando.executeQuery();

			while (rs.next()) {

				if (rs.next()) {
					perm = PermissoesDAO.selecionar(rs.getInt("FKNivel"));
					usu = new Funcionario(rs.getString("nomeUsu"), rs.getString(""), perm, rs.getBoolean("status"),
							rs.getBoolean("manterLogado"), rs.getBoolean("logado"));

					listaUsu.add(usu);
				}

			}

			rs.close();
			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return listaUsu;
	}

	public static void modificarUsuario(Funcionario usuario) {

		Permissoes perm = usuario.getNivel();

		try {

			String sql = "UPDATE usuario SET senha = ?, status = ?, FKNivel = ? where nomeUsu = ?";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			comando.setString(1, usuario.getSenha());
			comando.setBoolean(2, usuario.getStatus());
			comando.setInt(3, perm.getId());
			comando.setString(4, usuario.getLogin());

			comando.executeUpdate();

			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		JOptionPane.showMessageDialog(null, MySQL.getMSG());

	}

	public static int quantidadeUsuarios() {

		int quantidade = 0;

		try {

			String sql = "SELECT COUNT(*) from usuario";

			Conexao conex = new Conexao(MySQL.getURL(), MySQL.getDRIVER(), MySQL.getLOGIN(), MySQL.getSENHA());

			Connection con = conex.obterConexao();

			PreparedStatement comando = con.prepareStatement(sql);

			ResultSet rs = comando.executeQuery();

			quantidade = rs.getInt(0);

			comando.close();
			con.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		return quantidade;

	}

}