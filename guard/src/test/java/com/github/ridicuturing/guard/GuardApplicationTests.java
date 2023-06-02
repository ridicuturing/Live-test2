package com.github.ridicuturing.guard;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.github.ridicuturing.guard.manager.AiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootTest
class GuardApplicationTests {

	@Autowired
	AiManager aiManager;

	//@Test
	void contextLoads() {
	}

	@Value("${openai.api-key}")
	String apiKey;

	//@Test
	void askAiToReloadSchemeInitFile() throws InterruptedException {
		/*aiManager.ask(readEntities() + "\nhelp me for translating below java code to h2‘s sql of creating tables.drop table if existed.just give me the sql and say anything else!")
				.log()
				.subscribe(System.out::println);*/
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "10809");
		String code = readEntities();
		System.out.println(code);
		System.out.println("---------");
		String body = """
				{
				    "model": "gpt-3.5-turbo",
				    "messages": [
				        {"role": "user",
				            "content": "help user translate java code to h2’s create table sql,drop if existed.Beside the sql ,say nothing else!"},
				        {"role": "user",
				            "content": "%s"}
				    ]
				}
				""";
		String url = "https://api.openai.com/v1/chat/completions";
		String format = String.format(body, code);
		System.out.println(format);
		System.out.println("---------");
		HttpResponse httpResponse = HttpUtil.createPost(url).auth("").body(format, ContentType.JSON.toString()).execute();
		System.out.println(httpResponse);
		System.out.println("---------");
		Thread.sleep(1000*1000L);
	}

	public static void main(String[] args) throws InterruptedException {
		new GuardApplicationTests().askAiToReloadSchemeInitFile();
	}

	String readEntities() {
		File directory = new File("D:/tech/javaProject/guard/src/main/java/com/github/ridicuturing/guard/model/entity");
		File[] files = directory.listFiles();
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".java")) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					while ((line = reader.readLine()) != null) {
						if (!line.trim().startsWith("import ")) {
							sb.append(line.replaceAll("\n", "")).append(" ");
						}
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
