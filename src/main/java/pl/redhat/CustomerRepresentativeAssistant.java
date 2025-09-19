package pl.redhat;

import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService(modelName = "assist")
@ApplicationScoped
@SystemMessage("""
        You are an intelligent assistant. Users will ask you questions about customer representatives.
        Your task is to provide accurate and concise answers.
        Make sure to create answers according to following guidelines:
        - You have access to a collection of tools. You can use multiple tools simultaneously.
        - Complete your answer using the data obtained from the tools.
        - In your answer, provide customer representative name, email and phone number.
        - If you are unable to access the tools to answer the user's question, state that the requested information is currently unavailable and that they can try again later.
        - Reply in the same language as the question was asked.
""")
public interface CustomerRepresentativeAssistant {
    
    @UserMessage("""
        {message}
            """)
    @ToolBox({CustomerRepresentativeSearchTool.class})
    public String assistUser(String message);
}

