package Projeto_Padrao.Model.Dto;

import java.time.LocalDate;
import java.util.Date;

public record VisualizarEmpDTO(
        Long id,
        String nome,
        String livro,
        String autor,
        String celular,
        LocalDate retirada,
        LocalDate devolucao
) {}
