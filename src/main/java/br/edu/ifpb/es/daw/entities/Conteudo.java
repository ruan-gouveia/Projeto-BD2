package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_conteudo")
@Inheritance(strategy = InheritanceType.JOINED) // Metodo para herança com tabelas separadas
public class Conteudo {      //tlvz representar como classe abstrata dps...

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conteudo")
    private Long id;

    @Version
    @Column(name = "versao")
    private Long versao;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "tipo")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "id_administrador")
    private Administrador administrador;
    @ManyToMany
    @JoinTable(name = "tb_conteudo_genero",
            joinColumns = @JoinColumn(name = "id_conteudo"),
            inverseJoinColumns = @JoinColumn(name = "id_genero"))
    private List<Genero> genero = new ArrayList<>();

    @ManyToMany(mappedBy = "conteudos")
    private List<ListaDesejo> listaDesejos;
    @OneToMany(mappedBy = "conteudo")
    private List<Avaliacao> avaliacaos;
    @OneToMany(mappedBy = "conteudo")
    private List<Historico> historicos;

    public Conteudo() {}

    public Long getIdConteudo() { return id; }
    public void setIdConteudo(Long idConteudo) { this.id = idConteudo; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }

    public List<Genero> getGeneros() {
        return genero;
    }

    public void setGeneros(List<Genero> generos) {
        this.genero = generos;
    }

    public List<ListaDesejo> getListaDesejos() {
        return listaDesejos;
    }

    public void setListaDesejos(List<ListaDesejo> listaDesejos) {
        this.listaDesejos = listaDesejos;
    }

    public List<Avaliacao> getAvaliacaos() {
        return avaliacaos;
    }

    public void setAvaliacaos(List<Avaliacao> avaliacaos) {
        this.avaliacaos = avaliacaos;
    }

    public List<Historico> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<Historico> historicos) {
        this.historicos = historicos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conteudo conteudo = (Conteudo) o;
        return Objects.equals(id, conteudo.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Conteudo:" + id + ", titulo:" + titulo + "'}";
    }
}