package Projeto_Padrao.Model.Service;


import Projeto_Padrao.Model.Dto.EmprestimoDTO;
import Projeto_Padrao.Model.Dto.EmprestimosAtrasadosDTO;
import Projeto_Padrao.Model.Dto.VisualizarEmpDTO;
import Projeto_Padrao.Model.Entidade.Administrador;
import Projeto_Padrao.Model.Entidade.Emprestimo;
import Projeto_Padrao.Model.Exception.DataNotFoundException;
import Projeto_Padrao.Model.Repository.AdmRepository;
import Projeto_Padrao.Model.Repository.EmprestimoRepository;
import Projeto_Padrao.Model.Security.Config.SecurityConfiguration;
import Projeto_Padrao.Model.Security.JwtTokenService;
import Projeto_Padrao.Model.Security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service("AdmServicePricipal")
public class AdmService {

    final private EmprestimoRepository emprestimoRepository;
    final private AuthenticationManager authenticationManager;
    final private JwtTokenService jwtTokenService;
    final private AdmRepository admRepository;
    final private SecurityConfiguration securityConfiguration;

    public AdmService(EmprestimoRepository emprestimoRepository, AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, AdmRepository admRepository, SecurityConfiguration securityConfiguration) {
        this.emprestimoRepository = emprestimoRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.admRepository = admRepository;
        this.securityConfiguration = securityConfiguration;
    }

    //LISTAR TODOS OS REGISTROS
    public List<VisualizarEmpDTO> ListarEmprestimos() {
        List<Emprestimo> lista = emprestimoRepository.findAll();

        if (lista.isEmpty()) {
            throw new DataNotFoundException("EMPRESTIMOS NÃO ENCONTRADOS");
        }

        return lista.stream().map(e -> new VisualizarEmpDTO(
                e.getId(),
                e.getNome(),
                e.getLivro(),
                e.getAutor(),
                e.getCelular()
        )).toList();
    }

    public List<EmprestimosAtrasadosDTO> EmprestimosAtrasados() {
        List<EmprestimosAtrasadosDTO> listaAtrasados = emprestimoRepository.findAll().stream()
                .filter(e -> LocalDate.now().isAfter(e.getRetirada().plusMonths(1)))
                .filter(e -> !e.isDevolvido())
                .map(e -> new EmprestimosAtrasadosDTO(
                        e.getId(),
                        e.getCelular(),
                        e.getNome(),
                        e.getLivro(),
                        e.getAutor()
                ))
                .collect(Collectors.toList());

        if (listaAtrasados.isEmpty()) {
            throw new DataNotFoundException("NÃO EXISTEM EMPRESTIMOS ATRASADOS!");
        }
        return listaAtrasados;
    }

    public List<EmprestimosAtrasadosDTO> EmprestimosPendentes() {
        List<EmprestimosAtrasadosDTO> listaPendentes = emprestimoRepository.findAll().stream()
                .filter(e -> !e.isDevolvido())
                .map(e -> new EmprestimosAtrasadosDTO(
                        e.getId(),
                        e.getCelular(),
                        e.getNome(),
                        e.getLivro(),
                        e.getAutor()
                ))
                .collect(Collectors.toList());

        if (listaPendentes.isEmpty()) {
            throw new DataNotFoundException("NÃO EXISTEM EMPRESTIMOS PENDENTES!");
        }
        return listaPendentes;
    }
}
