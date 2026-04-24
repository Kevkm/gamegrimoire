import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { fetchSteamGames } from '../services/steamService';
import api from '../services/api';
import Navbar from '../components/Navbar';
import StatsBar from '../components/StatsBar';
import PlatformCard from '../components/PlatformCard';
import GameCard from '../components/GameCard';
import './Dashboard.css';

function Dashboard() {
  const { user,logout } = useAuth();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [connections, setconnections] = useState([]);
  const[steamId, setSteamId] = useState('');
  const[linkLoading, setLinkLoading] = useState(false);
  const[linkError, setLinkError] = useState(null);
  const[linkSuccess, setLinkSuccess] = useState(false);
  
  //load games from steam
  useEffect(() => {
    api.get('/api/platforms')
      .then(response => {
        const data = Array.isArray(response.data) ? response.data : [];
        setconnections(response.data);
        const steam = response.data.find(c => c.platform === 'STEAM' && c.active);
        if (steam) {
          loadGames(steam.platformUserId);
        } else {
          setLoading(false);
        }
      })
      .catch(() => setLoading(false));
  }, []);
function loadGames(id) {
  setLoading(true);
  setError(null);
  fetchSteamGames(id)
    .then(res => {
      setGames(res.data);
      setLoading(false);
    })
    .catch(() => {
      setError('Failed to load games');
      setLoading(false);
    });
  }

  async function handleLinkSteam(e) {
    e.preventDefault();
    setLinkLoading(true);
    setLinkError(null);
    setLinkSuccess(false);
    try {
      await api.post(`/api/platforms/link?platform=STEAM&platformUserId=${steamId}`);
      setLinkSuccess(true);
      loadGames(steamId);
    } catch (err) {
      setLinkError('Failed to link Steam account');
    } finally {
      setLinkLoading(false);
    }
  }

  const steamConnected = connections.find(c => c.platform === 'STEAM' && c.active);

  return (
    <div className="dashboard">
      <Navbar />
      <div className="dashboard-content">

          {/* Platform Connections */}
          <section className="dashboard-section dashboard-section--platforms">
          <div className="platforms-sidebar">
              <PlatformCard
                platform="STEAM"
                connected={!!steamConnected}
                platformUserId={steamConnected?.platformUserId}
                steamId={steamId}
                setSteamId={setSteamId}
                onLink={handleLinkSteam}
                linkLoading={linkLoading}
                linkError={linkError}
                linkSuccess={linkSuccess}
              />
              <PlatformCard platform="XBOX" connected={false} />
              <PlatformCard platform="EPIC" connected={false} />
              </div>
          </section>

         <div className="dashboard-section--main">
        {/* Games List */}
        <section className="dashboard-section">
          <div className="section-header">
            <h2 className="section-title">Your Library</h2>
            <span className="section-count">{games.length} games</span>
          </div>
          {loading && <p className="dashboard-message">Loading games...</p>}
          {error && <p className="dashboard-message dashboard-message--error">{error}</p>}
          {!loading && !error && games.length === 0 && (
            <p className="dashboard-message">No games found. Connect a platform to see your library.</p>
          )}
          <div className="games-grid">
            {games.map(game => (
              <GameCard key={game.id} game={game} />
            ))}
          </div>
        </section>
      </div>
      {/* Stats Bar */}
      <aside className="dashboard-section--stats">
        <StatsBar games={games} connections={connections}/>
      </aside>
    </div>
    </div>
  );
}

export default Dashboard;