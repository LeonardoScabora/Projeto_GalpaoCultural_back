package Projeto_Padrao.Model.Service;

import Projeto_Padrao.Model.Dto.EmprestimoDTO;
import Projeto_Padrao.Model.Dto.EmprestimosAtrasadosDTO;
import Projeto_Padrao.Model.Dto.VisualizarEmpDTO;
import Projeto_Padrao.Model.Entidade.Emprestimo;
import Projeto_Padrao.Model.Exception.DataNotFoundException;
import Projeto_Padrao.Model.Repository.EmprestimoRepository;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.genai.Client;

import java.time.LocalDate;
import java.util.List;

@Service("emprestimoServicePrincipal")
public class EmprestimoService {

    final private EmprestimoRepository emprestimoRepository;

    @Value("${API_KEY_GEMINI_PROD}")
    private String apiKey;

    public EmprestimoService(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    /// Listar todos os emprestimo pelo telefone
    public List<VisualizarEmpDTO> VerEmprestimos(String celular) {
        List<Emprestimo> listaDeLivro = emprestimoRepository.findAllByCelularAndDevolvidoIsFalse(celular);
        return listaDeLivro.stream()
                .map(e -> new VisualizarEmpDTO(
                        e.getId(),
                        e.getNome(),
                        e.getLivro(),
                        e.getAutor(),
                        e.getCelular(),
                        e.getRetirada(),
                        e.getDevolucao()
                )).toList();
    }

    public void AdicionarEmprestimo(EmprestimoDTO emprestimoNovo) {
        try {
            Emprestimo emprestimoRevisado = new Emprestimo(emprestimoNovo);

            ///FAZER BUSCA SE O NOME DO LIVRO JÁ NÃO ESTÁ NO BANCO PARA NÃO UTILIZAR A IA
            Emprestimo buscarNome = emprestimoRepository.findByLivroContainingIgnoreCaseAndDevolvidoIsTrue(emprestimoNovo.livro());

            if (buscarNome == null) {
                emprestimoRevisado.setLivro(CorrigirNomes(emprestimoNovo.livro()));
                emprestimoRevisado.setAutor(CorrigirNomes(emprestimoNovo.autor()));
            } else {
                emprestimoRevisado.setLivro(buscarNome.getLivro());
                emprestimoRevisado.setAutor(buscarNome.getAutor());
            }

            emprestimoRepository.save(emprestimoRevisado);
        } catch (Exception e) {
            throw new DataNotFoundException("NÃO FOI POSSÍVEL REGISTRAR ESSE EMPRÉSTIMO!");
        }
    }

    public void DevolverLivro(Long id) {
        Emprestimo registro = emprestimoRepository.findById(id).filter(e -> !e.isDevolvido()).orElseThrow(() -> new DataNotFoundException(id));
        registro.setDevolucao(LocalDate.now());
        registro.setDevolvido(true);
        emprestimoRepository.save(registro);
    }

    public String CorrigirNomes(String nome) {
        Client client = Client.builder()
                .apiKey(apiKey)
                .build();

        String prompt = """
                Você é uma IA especializada em literatura. Corrija e padronize nomes de livros e autores digitados com erros ou abreviações.
                
                Regras:
                1. Responda sempre em CAPSLOCK.
                2. Para livros, retorne o título correto mais conhecido.
                   Ex.: DON CASMURO -> DOM CASMURRO; ALNISTA -> O ALIENISTA.
                3. Para autores, retorne o nome completo correto.
                   Ex.: MAÇADO -> MACHADO DE ASSIS.
                4. A entrada pode ser só livro ou só autor.
                5. Se houver dúvida, retorne a forma mais provável e conhecida.
                
                Entrada: "%s"
                
                Saída:
                """.formatted(nome);

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );

        return response.text().trim();
    }
}
