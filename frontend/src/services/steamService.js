import api from './api';

export const fetchSteamGames = (email, steamId) => {
    return api.get('/api/steam/games', {
        params: { email, steamId }
    });
};