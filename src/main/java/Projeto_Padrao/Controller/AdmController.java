package Projeto_Padrao.Controller;

import Projeto_Padrao.Model.Dto.Autenticacao.CreateUserDto;
import Projeto_Padrao.Model.Dto.Autenticacao.LoginUserDto;
import Projeto_Padrao.Model.Dto.Autenticacao.RecoveryJwtTokenDto;
import Projeto_Padrao.Model.Dto.EmprestimoDTO;
import Projeto_Padrao.Model.Dto.EmprestimosAtrasadosDTO;
import Projeto_Padrao.Model.Dto.VisualizarEmpDTO;
import Projeto_Padrao.Model.Entidade.Emprestimo;
import Projeto_Padrao.Model.Service.AdmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adm")
public class AdmController {

    final private AdmService admService;

    public AdmController(AdmService admService) {
        this.admService = admService;
    }

    @GetMapping(path = "/todosEmprestimos")
    public ResponseEntity<List<VisualizarEmpDTO>> ListarEmprestimos() {
        return ResponseEntity.status(HttpStatus.OK).body(admService.ListarEmprestimos());
    }

    @GetMapping(path = "/atrasados")
    public ResponseEntity<List<VisualizarEmpDTO>> EmprestimosAtrasados(){
        return ResponseEntity.status(HttpStatus.OK).body(admService.EmprestimosAtrasados());
    }

    @GetMapping(path = "/pendentes")
    public ResponseEntity<List<VisualizarEmpDTO>> EmprestimosPendentes(){
        return ResponseEntity.status(HttpStatus.OK).body(admService.EmprestimosPendentes());
    }
}