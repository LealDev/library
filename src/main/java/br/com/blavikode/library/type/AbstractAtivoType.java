package br.com.blavikode.library.type;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import static br.com.blavikode.library.ApplicationConstants.ATIVO_ESTA_NULO;
import static br.com.blavikode.library.ApplicationConstants.PROP_ATIVO;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;

@MappedSuperclass
public class AbstractAtivoType extends AbstractType{
    private static final long serialVersionUID = 1L;

    private boolean ativo;
    @NotNull(message = ATIVO_ESTA_NULO)
    @Column(name = PROP_ATIVO)
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(final Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public void prePersist() {
        if (!allNotNull(getAtivo())) {
            setAtivo(true);
        }
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        if (!allNotNull(getAtivo())) {
            setAtivo(true);
        }
        super.preUpdate();
    }
}
