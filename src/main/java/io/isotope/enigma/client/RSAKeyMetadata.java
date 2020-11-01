package io.isotope.enigma.client;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RSAKeyMetadata {

    private String name;
    private String padding;
    private Integer size;
    private String blockCipherMode;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Boolean active;

}
