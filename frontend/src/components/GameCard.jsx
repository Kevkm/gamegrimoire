import { useState } from 'react';
import './GameCard.css';

export default function GameCard({ game }) {
    const [imgError, setImgError] = useState(false);

    // Use iconUrl directly for Xbox, build Steam CDN URL for Steam
    const imgURL = imgError ? null
        : game.platform === 'XBOX' && game.iconUrl
            ? game.iconUrl
            : game.platformGameId
                ? `https://cdn.akamai.steamstatic.com/steam/apps/${game.platformGameId}/header.jpg`
                : null;
    
    const hours = game.playtimeMinutes
        ? (game.playtimeMinutes / 60).toFixed(1)
        : '0';

    const lastPlayed = game.lastPlayed
        ? new Date(game.lastPlayed).toLocaleDateString('en-US', {month: 'short', year: 'numeric'})
        : null;

    const achievements = game.achievmentsEarned > 0
    ? `${game.achievmentsEarned} / ${game.achievmentsTotal} achievements`
    : null;

    return (
        <div className="game-card">
            <div className="game-card-img.wrap">
                {imgURL ? (
                    <img
                        src={imgURL}
                        alt={game.name}
                        className={`game-card-img ${game.platform === 'XBOX' ? 'game-card-img--xbox' : ''}`}
                        onError={() => setImgError(true)}
                    />
                ) : (
                    <div className="game-card-img-placeholder">
                        <span>{game.name?.charAt(0)}</span>
                    </div>
                )}
                <div className="game-card-overlay" />
                </div>
                <div className="game-card-body">
                    <h3 className="game-card-title">{game.name}</h3>
                    <div className="game-card-meta">
                        <span className="game-card-pill">{hours} hrs</span>
                        {lastPlayed && <span className="game-card-pill game-card-pill--muted">{lastPlayed}</span>}
                    </div>
                    {achievements && (
                        <div className="game-card-achievements">
                            <div className="game-card-achievements-bar">
                                <div
                                    className="game-card-achievements-fill"
                                    style={{ width: game.achievmentsTotal > 0
                                        ? `${(game.achievmentsEarned / game.achievmentsTotal) * 100}%`
                                            : '0%'
                                        }}
                        />
            </div>
            <span className="game-card-achievements-label">{achievements}</span>
        </div>
    )}
</div>
        </div>
    );
}
