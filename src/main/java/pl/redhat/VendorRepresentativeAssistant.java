package pl.redhat;

import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

//        - If you search for vendors solutions here is the list of all available solutions: AI-powered networking, AIOps, Archer Engage, Archer IT Risk Management, Archer IT Security Vulnerabilities Program, Archer Issues Management, Archer PCI Management, Archer Platform, Backup and recovery, Business Continuity Management, Cleanroom Recovery, Cloud data management, Cloud networking, Cloud-managed networking, Cloud-based SaaS, CloudVision, Cloud-native networking, Cyber Incident and Breach Response, Cyber resilience, Data protection, Data security, Dynamic segmentation, Edge-to-cloud security, Evolv, Extensible Operating System, Ethernet switches, Immutable storage, Identity Resilience, Integrated Risk Management (IRM) Solutions, IT & Security Risk Management, IT Vendor Risk Management, Machine Learning, Network as a Service, Network automation, Network management, Network operating system, Network switches, Network segmentation, On-Premises, Operational Resilience, Private Hosted, Programmability, Ransomware protection, Rebuild automation, Reporting and Dashboards, Risk Analytics, Risk Quantification, SD-WAN, Software-defined networking, Third Party Risk Management, Unified infrastructure, Workflows, Zero Trust
//        - Alway match solutions asked by user with the list above


@RegisterAiService(modelName = "assist")
@ApplicationScoped
//@SystemMessage("${vendor-assistant.system-message}")
@SystemMessage("""
        You are an intelligent client assistant. Users will ask you questions about IT vendors, their solutions and sales and presales representatives from distributor named Arrow.
        Your task is to provide accurate and concise answers.
        Make sure to create answers according to following guidelines:
        - You have access to a collection of tools. You can use multiple tools simultaneously.
        - Complete your answer using the data obtained from the tools.
        - If you are asked about specific role i.e. sales or presales provide only name of that role person
        - If you are unable to access the tools to answer the user's question, state that the requested information is currently unavailable and that they can try again later.
        - Reply in the same language as the question was asked.
        - If you are asked about vendors answer only with vendor name
        - If there is any difference between requested vendor, sales or presales name and the name used in response please mention that
        """)
public interface VendorRepresentativeAssistant {    
    @UserMessage("{message}")
    @ToolBox({VendorRepresentativeSearchTool.class})
    public Multi<String> assistUser(String message);
}

