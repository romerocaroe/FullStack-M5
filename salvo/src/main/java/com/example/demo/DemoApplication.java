package com.example.demo;

import com.example.demo.models.*;
import com.example.demo.repositories.GamePlayerRepository;
import com.example.demo.repositories.GameRepository;
import com.example.demo.repositories.PlayerRepository;
import com.example.demo.repositories.ShipRepository;
import com.example.demo.repositories.SalvoRepository;
import com.example.demo.repositories.ScoreRepository;
import java.util.Arrays;
import java.util.Date;

import jdk.nashorn.internal.objects.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		System.out.println("Hello World!");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository) {
		return (args) -> {
			//to Player repository
			Player player1 = new Player("romerocaroe@gmail.com", passwordEncoder().encode("888"));
			playerRepository.save(player1);

			Player player2 = new Player("cerxarg@gmail.com",passwordEncoder().encode("888"));
			playerRepository.save(player2);

			Player player3 = new Player("Dani@gmail.com",passwordEncoder().encode("888"));
			playerRepository.save(player3);

			//to Game repository
			Game game1 = new Game();
			Game game2 = new Game();
				 game2.setCreationDate(Date.from(game2.getCreationDate().toInstant().plusSeconds(3600)));
			Game game3 = new Game();
				 game3.setCreationDate(Date.from(game3.getCreationDate().toInstant().plusSeconds(7200)));

				 gameRepository.save(game1);
				 gameRepository.save(game2);
				 gameRepository.save(game3);

			//To GamePlayer Repository

			GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
			gamePlayerRepository.save(gamePlayer1);
			GamePlayer gamePlayer2 = new GamePlayer(player2, game1);
			gamePlayerRepository.save(gamePlayer2);
			GamePlayer gamePlayer3 = new GamePlayer(player3, game3);
			gamePlayerRepository.save(gamePlayer3);

			String battleship = "Battleship";
			String submarine = "Submarine";
			String destroyer = "Destroyer";
			String patrolBoat = "Patrol Boat";

			List<String> locations1 = new ArrayList<>();
			locations1.add("H1");
			locations1.add("H2");
			locations1.add("H3");

			Ship ship1 = new Ship(destroyer, locations1, gamePlayer1);
			Ship ship2 = new Ship(submarine, Arrays.asList("E1", "F1", "G1"), gamePlayer1);
			Ship ship3 = new Ship(patrolBoat, Arrays.asList("B4", "B5"), gamePlayer1);
			Ship ship4 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer2);
			Ship ship5 = new Ship(patrolBoat, Arrays.asList("F1", "F2"), gamePlayer2);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);

			Salvo salvo1 = new Salvo(1,Arrays.asList("B5", "C5", "D5", "H1", "F1"),gamePlayer1);
			Salvo salvo2 = new Salvo(1,Arrays.asList("B5", "C5", "D5", "H1", "F1"),gamePlayer2);

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);

		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter{
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByEmail(inputName);
			if (player != null) {
				return new User(player.getEmail(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/game_view/*").hasAuthority("USER")
				.antMatchers("/h2-console/***").permitAll();

		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}



