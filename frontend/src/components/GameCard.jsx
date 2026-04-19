import { useState } from 'react';
import './GameCard.css';

export default function GameCard({ game }) {
    const [imgError, setImgError] = useState(false);
    const steamAppId = game.platformId; // Assuming platformId is the Steam App ID
    const imgURL = steamAppId && !imgError
        ? `https://cdn.akamai.steamstatic.com/steam/apps/${steamAppId}/header.jpg`
        : null;

    const hours = game.playtimeMinutes
        ? (game.playtimeMinutes / 60).toFixed(1)
        : '0';

    const lastPlayed = game.lastPlayed
        ? new Date(game.lastPlayed).toLocaleDateString('en-US', {month: 'short', year: 'numeric'})
        : null;

    return (
        <div className="game-card">
            <div className ="game-card-img.wrap">
                {imgURL ? (
                    <img
                        src={imgURL}
                        alt={game.name}
                        className="game-card-img"
                        onError={() => setImgError(true)}
                    />
                ) : (
                    <div className="game-card-placeholder">
                        <span>{game.name?.charAt(0)}</span>
                    </div>
                )}
                <div className="game-card-overlay" />
                </div>
                <div className="game-card-body">
                    <h3 className="game-card-title">{game.name}</h3>
                    <div className="game-card-meta">
                        <span className="game-card-pill">{hours} hours</span>
                        {lastPlayed && <span className="game-card-pill game-card-pill--muted">{lastPlayed}</span>}
                    </div>
                </div>
        </div>
    );
}
