import { useState } from 'react';
import { FaSteam, FaXbox } from 'react-icons/fa';
import { SiEpicgames } from 'react-icons/si';
import './PlatformCard.css';

export default function PlatformCard({ platform, connected, platformUserId, steamId, setSteamId, onLink, linkLoading, linkError, linkSuccess, onXboxConnect, onXboxDisconnect, onSteamDisconnect }) {
  const [expanded, setExpanded] = useState(false);

  const icons = {
    STEAM: <FaSteam size={28} />,
    XBOX: <FaXbox size={28} />,
    EPIC: <SiEpicgames size={28} />,
  };

  const labels = {
    STEAM: 'Steam',
    XBOX: 'Xbox',
    EPIC: 'Epic Games',
  };

  const comingSoon = platform === 'EPIC';

  return (
    <div className={`platform-card ${connected ? 'platform-card--connected' : ''} ${expanded ? 'platform-card--expanded' : ''}`}>
      <button
        className="platform-icon-btn"
        onClick={() => setExpanded(e => !e)}
        title={labels[platform]}
      >
        <span className={`platform-icon ${connected ? 'platform-icon--connected' : ''}`}>
          {icons[platform]}
        </span>
        {connected && <span className="platform-dot" />}
      </button>

      {expanded && (
        <div className="platform-card-panel">
          <div className="platform-card-header">
            <span className="platform-name">{labels[platform]}</span>
            {connected && <span className="platform-badge">Connected</span>}
            {comingSoon && !connected && <span className="platform-badge platform-badge--soon">Soon</span>}
          </div>

          {connected && (
    <div>
        <p className="platform-id">ID: {platformUserId}</p>
        {platform === 'STEAM' && (
            <button 
                className="platform-btn platform-btn--disconnect"
                onClick={onSteamDisconnect}>
                Disconnect
            </button>
        )}
    </div>
)}


          {!connected && !comingSoon && platform !== 'XBOX' && (
            <form className="platform-form" onSubmit={onLink}>
              <input
                type="text"
                className="platform-input"
                placeholder="Enter your Steam ID"
                value={steamId}
                onChange={e => setSteamId(e.target.value)}
                required
              />
              <button type="submit" className="platform-btn" disabled={linkLoading}>
                {linkLoading ? 'Linking...' : 'Link Account'}
              </button>
              {linkError && <p className="platform-error">{linkError}</p>}
              {linkSuccess && <p className="platform-success">Linked!</p>}
            </form>
          )}

        {platform === 'XBOX' && !connected && (
          <button className="platform-btn" onClick={onXboxConnect}>
            Connect Xbox
          </button>
          )}

        {platform === 'XBOX' && connected && (
          <button className="platform-btn platform-btn--disconnect" onClick={onXboxDisconnect}>
            Disconnect
          </button>
          )}

          {comingSoon && !connected && (
            <p className="platform-coming-soon">Coming soon</p>
          )}
        </div>
      )}
    </div>
  );
}
