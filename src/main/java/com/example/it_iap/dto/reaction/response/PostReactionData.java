package com.example.it_iap.dto.reaction.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostReactionData {
    int totalLove;

    int totalHaha;

    int totalWow;

    public PostReactionData(
            Long totalLove,
            Long totalHaha,
            Long totalWow
    ) {
        this.totalLove = totalLove == null ? 0 : totalLove.intValue();
        this.totalHaha = totalHaha == null ? 0 : totalHaha.intValue();
        this.totalWow = totalWow == null ? 0 : totalWow.intValue();
    }
}
