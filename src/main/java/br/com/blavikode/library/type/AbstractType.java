package br.com.blavikode.library.type;

import static br.com.blavikode.library.ApplicationConstants.*;
import static java.lang.System.currentTimeMillis;
import static jakarta.persistence.GenerationType.IDENTITY;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.blavikode.library.geral.ICallback;

@MappedSuperclass
public abstract class AbstractType implements Serializable, ICallback {
    private static final long serialVersionUID = 2743591570784179362L;
    private Timestamp criacao;
    private long id;
    private Timestamp modificacao;
    private Integer versao;

    @PrePersist
    private void prePersistType() {
        criacao = new Timestamp(currentTimeMillis());
        modificacao = new Timestamp(currentTimeMillis());
        prePersist();
    }

    @PreUpdate
    private void preUpdateType() {
        modificacao = new Timestamp(currentTimeMillis());
        preUpdate();
    }

    @JsonIgnore
    @Transient
    public boolean flagAbstractTypeDetached() {
        return id > 0;
    }

    @JsonIgnore
    @Transient
    public boolean flagNotAbstractTypeDetached() {
        return !flagAbstractTypeDetached();
    }

    @JsonIgnore
    @Column(name = PROP_CRIACAO, updatable = false)
    public Timestamp getCriacao() {
        return criacao;
    }

    public void setCriacao(final Timestamp criacao) {
        this.criacao = criacao;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = PROP_ID, unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @JsonIgnore
    @Column(name = PROP_MODIFICACAO)
    public Timestamp getModificacao() {
        return modificacao;
    }

    public void setModificacao(final Timestamp modificacao) {
        this.modificacao = modificacao;
    }

    @Version
    @Column(name = PROP_VERSAO)
    public Integer getVersao() {
        return versao;
    }

    public void setVersao(final Integer versao) {
        this.versao = versao;
    }

    @Transient
    @JsonIgnore
    public boolean flagTransfer() {
        return !allNotNull(getVersao()) && flagAbstractTypeDetached();
    }

    @Transient
    @JsonIgnore
    public boolean flagNotTransfer() {
        return allNotNull(getVersao()) && flagAbstractTypeDetached();
    }

    @Override
    public String toString() {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }

}
