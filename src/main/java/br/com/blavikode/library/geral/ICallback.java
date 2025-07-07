package br.com.blavikode.library.geral;

import jakarta.persistence.Transient;


public interface ICallback {
    @Transient
    public default void prePersist() {
    }

    @Transient
    public default void preUpdate() {
    }
}

