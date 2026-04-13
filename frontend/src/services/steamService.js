import api from './api';

export const fetchSteamGames = (steamId) => {
    return api.get('/api/steam/games', {
        params: { steamId }
    });
};