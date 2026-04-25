import api from './api';

export const getXboxStatus = () => api.get('/api/xbox/status');
export const connectXbox = () => {
    api.get('/api/xbox/connect')
        .then(res => {
            window.location.href = res.data.authUrl;
        })
        .catch(err => console.error('Failed to connect Xbox:', err));
};

export const disconnectXbox = () => api.delete('/api/xbox/disconnect');

export const fetchXboxGames = () => api.get('/api/xbox/games');