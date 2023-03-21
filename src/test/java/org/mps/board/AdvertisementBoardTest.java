package org.mps.board;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdvertisementBoardTest {

    AdvertisementBoard ab;

    @BeforeEach
    public void setup()
    {
        ab = new AdvertisementBoard();
    }

    @AfterEach
    public void shutdown()
    {
        ab = null;
    }

    @Test
    @DisplayName("1. Comprobar que inicialmente hay un anuncio en el tablón.")
    void test1()
    {
        assertEquals(1, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("2. Crear un anuncio por parte de \"THE Company\", insertarlo y comprobar que se ha incrementado en uno el número de anuncios del tablón.")
    void test2()
    {
        Advertisement ad = new Advertisement("Second test", "Ad for the second test", "THE Company");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        ab.publish(ad, adDB, payment);

        assertEquals(2, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("3. Publicar un anuncio por parte del anunciante \"Pepe Gotera y Otilio\" y comprobar que, si está en la base de datos de anunciantes pero no tiene saldo, el anuncio no se inserta, lo que se determina observando que el número de anuncios no aumenta.")
    void test3()
    {
        Advertisement ad = new Advertisement("Third test", "Ad for the third test", "Pepe Gotera y Otilio");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        when(adDB.advertiserIsRegistered("Pepe Gotera y Otilio")).thenReturn(true);
        when(payment.advertiserHasFunds("Pepe Gotera y Otilio")).thenReturn(false);

        ab.publish(ad, adDB, payment);

        assertEquals(1, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("4. Publicar un anuncio por parte de un anunciante llamado \"Robin Robot\", asumiendo que está en la base de datos de anunciantes, que tiene saldo y finalmente comprobando que se ha realizado el cargo.")
    void test4()
    {
        Advertisement ad = new Advertisement("Fourth test", "Ad for the fourth test", "Robin Robot");
        AdvertiserDatabase adDB = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway payment = Mockito.mock(PaymentGateway.class);

        when(adDB.advertiserIsRegistered("Robin Robot")).thenReturn(true);
        when(payment.advertiserHasFunds("Robin Robot")).thenReturn(true);
        payment.chargeAdvertiser("Robin Robot");
        verify(payment).chargeAdvertiser("Robin Robot");

        ab.publish(ad, adDB, payment);

        assertEquals(2, ab.numberOfPublishedAdvertisements());
    }

    @Test
    @DisplayName("5. Publicar dos anuncios distintos por parte de \"THE Company\", borrar el primero y comprobar que si se busca ya no está en el tablón.")
    void test5()
    {

    }

    @Test
    @DisplayName("6. Comprobar que si se intenta publicar un anuncio que ya existe (mismo título y mismo anunciante), no se realiza la publicación y no se realiza ningún cargo. El anunciante no debe ser \"THE Company\". Para pasar esta prueba hay que modificar la clase AdvertisementBoard.")
    void test6()
    {

    }

    @Test
    @DisplayName("7. Comprobar que si se intenta publicar un anuncio por parte del anunciante \"Tim O'Theo\" y el tablón está lleno se eleva la excepción AdvertisementBoardException.  Para pasar esta prueba hay que modificar la clase AdvertisementBoard.")
    void test7()
    {

    }
}
