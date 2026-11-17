package ht.mbds.tarrouzi.tp2_tarrouzi.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;

import jakarta.enterprise.context.Dependent;

import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {

    private static final long serialVersionUID = 1L;

    private String systemRole;

    private transient ChatMemory chatMemory;
    private transient Assistant assistant;

    private void initialiserSiNecessaire() {

        if (assistant != null) {
            return;
        }

        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable GEMINI_API_KEY n'est pas définie."
            );
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash")
                .temperature(0.2)
                .build();

        chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        if (systemRole != null && !systemRole.isBlank()) {
            chatMemory.add(SystemMessage.from(systemRole));
        }

        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String systemRole) {

        initialiserSiNecessaire();

        this.systemRole = systemRole;

        chatMemory.clear();

        if (systemRole != null && !systemRole.isBlank()) {
            chatMemory.add(SystemMessage.from(systemRole));
        }
    }

    public String chat(String question) {

        initialiserSiNecessaire();

        return assistant.chat(question);
    }
}