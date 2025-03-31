package com.medcare;
import com.medcare.ui.LoginFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;


@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class,
		org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class,
		org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration.class
})
//clasa principala care configureaza interfata grafica si dezactiveaza functionalitatile web
public class MedcareClinicApplication
{

	public static void main(String[] args)
	{
		System.setProperty("java.awt.headless", "false");

		ConfigurableApplicationContext context = new SpringApplicationBuilder(MedcareClinicApplication.class)
				.headless(false)
				.web(org.springframework.boot.WebApplicationType.NONE)
				.run(args);

		SwingUtilities.invokeLater(() -> {
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			LoginFrame loginFrame = context.getBean(LoginFrame.class);
			loginFrame.setVisible(true);
		});
	}
}