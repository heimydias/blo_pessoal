package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@BeforeAll
	void start() {
		
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", "-" ));
	}
	
	@Test
	@DisplayName("😒 Deve Cadastrar um novo Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Douglas Guine" , "douglasguine@email.com", "12345678", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar" , HttpMethod.POST, corpoRequisicao , Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("😒 Não Deve perimitir a duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria", "maria@email.com", "rootroot", "-" ));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Maria" , "maria@email.com", "12345678", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar" , HttpMethod.POST, corpoRequisicao , Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
    @DisplayName("Atualizar um Usuário")
    public void deveAtualizarUmUsuario() {

        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, 
            "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));

        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
            "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "-");

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
            .withBasicAuth("root@root.com", "rootroot")
            .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);


    }
	
	@Test
	@DisplayName("😒 Deve Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Douglas", "douglas@email.com", "12345678", "-" ));
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Andresa", "andresa@email.com", "12345678", "-" ));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all" , HttpMethod.GET, null , String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
    @DisplayName("😁 Deve listar usuário por id")
    public void deveListarUmUsuarioPorId() {

        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                "ana", "ana@email.com.br", "12345678", "-"));//cadastrar user

        Long usuario = usuarioCadastrado.get().getId();


        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/" + usuario, HttpMethod.GET, null, String.class);

            assertEquals(HttpStatus.OK, resposta.getStatusCode());// se resposta existe, mostrar status
    }

    @Test
    @DisplayName("😁 Deve autenticar o login")
    public void deveAutenticarlogin() {

        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                "Joao", "joao@email.com.br", "12345678", "-"));//cadastrar user

        //importar a model UsuarioLogin
        HttpEntity<UsuarioLogin> loginUser = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L,
                "", "joao@email.com.br", "12345678", "-", ""));//cadastrar user

        ResponseEntity<UsuarioLogin> resposta = testRestTemplate
                .exchange("/usuarios/logar", HttpMethod.POST, loginUser, UsuarioLogin.class);
            assertEquals(HttpStatus.OK, resposta.getStatusCode());// se autenticacao teve exito, mostrar status
    }
}