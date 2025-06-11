package com.acon.server.member.domain.entity;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.member.domain.enums.Cuisine;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.domain.enums.FavoriteSpot;
import com.acon.server.member.domain.enums.SpotStyle;
import com.acon.server.spot.domain.enums.SpotType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Preference {

    // TODO: 매직 넘버 yml로 옮기기
    private static final int FAVORITE_CUISINE_RANK_SIZE = 3;
    private static final int FAVORITE_SPOT_RANK_SIZE = 4;

    private final Long memberId;

    private List<DislikeFood> dislikeFoodList;
    private List<Cuisine> favoriteCuisineRank;
    private SpotType favoriteSpotType;
    private SpotStyle favoriteSpotStyle;
    private List<FavoriteSpot> favoriteSpotRank;

    @Builder
    public Preference(
            Long memberId,
            List<DislikeFood> dislikeFoodList,
            List<Cuisine> favoriteCuisineRank,
            SpotType favoriteSpotType,
            SpotStyle favoriteSpotStyle,
            List<FavoriteSpot> favoriteSpotRank
    ) {
        validateFavoriteSpotRank(favoriteSpotRank);
        validateFavoriteCuisineRank(favoriteCuisineRank);
        this.memberId = memberId;
        this.dislikeFoodList = dislikeFoodList;
        this.favoriteCuisineRank = favoriteCuisineRank;
        this.favoriteSpotType = favoriteSpotType;
        this.favoriteSpotStyle = favoriteSpotStyle;
        this.favoriteSpotRank = favoriteSpotRank;
    }

    private void validateFavoriteSpotRank(List<FavoriteSpot> favoriteSpotRank) {
        if (favoriteSpotRank.size() != FAVORITE_SPOT_RANK_SIZE) {
            throw new BusinessException(ErrorType.INVALID_FAVORITE_SPOT_RANK_SIZE_ERROR);
        }
    }

    private void validateFavoriteCuisineRank(List<Cuisine> favoriteCuisineRank) {
        if (favoriteCuisineRank.size() != FAVORITE_CUISINE_RANK_SIZE) {
            throw new BusinessException(ErrorType.INVALID_FAVORITE_CUISINE_RANK_SIZE_ERROR);
        }
    }
}
