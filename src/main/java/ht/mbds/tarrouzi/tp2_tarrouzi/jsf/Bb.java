package ht.mbds.tarrouzi.tp2_tarrouzi.jsf;

import ht.mbds.tarrouzi.tp2_tarrouzi.llm.LlmClient;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class Bb implements Serializable {

    @Inject
    private LlmClient llmClient;
    @Inject
    private FacesContext facesContext;



    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    public Bb() {
    }


    public String getRoleSysteme() { return roleSysteme; }
    public void setRoleSysteme(String roleSysteme) { this.roleSysteme = roleSysteme; }

    public boolean isRoleSystemeChangeable() { return roleSystemeChangeable; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public String getConversation() { return conversation.toString(); }
    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    public String envoyer() {

        if (question == null || question.isBlank()) {
            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Question vide",
                    "Veuillez saisir une question."
            ));

            return null;
        }

        try {
            // Initialiser le rôle uniquement au début du chat
            if (roleSystemeChangeable) {

                String role = (roleSysteme == null || roleSysteme.isBlank())
                        ? "You are a helpful assistant."
                        : roleSysteme;

                llmClient.setSystemRole(role);
            }

            // Envoyer la question avec LangChain4j
            reponse = llmClient.chat(question);

            roleSystemeChangeable = false;

            // Ajouter l'échange à l'historique affiché
            afficherConversation();

        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erreur lors de l'appel au LLM",
                    "Impossible d'obtenir une réponse. " + e.getMessage()
            ));
        }

        return null;
    }


public String nouveauChat() {
    question = null;
    reponse = null;
    conversation = new StringBuilder();
    roleSystemeChangeable = true;

    return null;
}

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question)
                .append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();

            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;
            this.listeRolesSysteme.add(
                    new SelectItem(role.replaceAll("\\s+", " ").trim(), "Assistant")
            );
             role = """
                    You are the oracle of dephi , you answer questions as prophecies , comme celles dans percy jackson , nice rhymes
                    et legerement vague,not too too long .
                    """;
            this.listeRolesSysteme.add(
                    new SelectItem(role.replaceAll("\\s+", " ").trim(), "Oracle")
            );

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role.replaceAll("\\s+", " ").trim(), "Traducteur Anglais-Français")
            );

            role = """
                    Your are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role.replaceAll("\\s+", " ").trim(), "Guide touristique")
            );
        }
        return this.listeRolesSysteme;
    }
}