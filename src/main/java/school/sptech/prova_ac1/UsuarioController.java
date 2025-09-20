package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Erro: Email já cadastrado.");
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Erro: CPF já cadastrado.");
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        return usuarioOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(
            @RequestParam("nascimento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nascimento) {

        List<Usuario> usuariosFiltrados = usuarioRepository.findByDataNascimentoAfter(nascimento);

        if (usuariosFiltrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuariosFiltrados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        boolean emailExisteEmOutro = usuarioRepository.existsByEmailAndIdNot(usuario.getEmail(), id);
        if (emailExisteEmOutro) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Erro: Email já cadastrado em outro usuário.");
        }

        boolean cpfExisteEmOutro = usuarioRepository.existsByCpfAndIdNot(usuario.getCpf(), id);
        if (cpfExisteEmOutro) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Erro: CPF já cadastrado em outro usuário.");
        }

        usuario.setId(id);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return ResponseEntity.ok(usuarioAtualizado);
    }
}
