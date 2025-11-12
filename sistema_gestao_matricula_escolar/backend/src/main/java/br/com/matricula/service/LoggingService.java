package br.com.matricula.service;

import br.com.matricula.model.log.LogEntry;
import br.com.matricula.repository.LogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private final LogRepository logRepository;

    public LoggingService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logAction(String acao, String entidade, String entidadeId, Object detalhes) {
        String usuario = getCurrentUserLogin();
        LogEntry logEntry = new LogEntry(acao, entidade, entidadeId, usuario, detalhes);
        logRepository.save(logEntry);
    }

    private String getCurrentUserLogin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal != null) {
            return principal.toString();
        }
        return "ANONYMOUS";
    }
}