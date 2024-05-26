package com.patroclos.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class ThymeleafConfig {

	@Bean
	public SpringResourceTemplateResolver templateResolver(){
	    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	    templateResolver.setPrefix("classpath:/static/templates/");
	    templateResolver.setSuffix(".html");
	    templateResolver.setTemplateMode(TemplateMode.HTML);
	    templateResolver.setCacheable(true);
	    templateResolver.setCheckExistence(true);
	    return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine(){
	    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	    templateEngine.setTemplateResolver(templateResolver());
	    templateEngine.setEnableSpringELCompiler(true);
	    return templateEngine;
	}
	
	
	@Bean
	public ThymeleafViewResolver viewResolver(){
	    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
	    viewResolver.setTemplateEngine(templateEngine());
	    viewResolver.setOrder(1);
	    viewResolver.setViewNames(new String[] {".html", ".xhtml"});
	    return viewResolver;
	}

}
