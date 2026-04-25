import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { fetchSteamGames } from '../services/steamService';
import { getXboxStatus, connectXbox, disconnectXbox, fetchXboxGames} from '../services/xboxService';
import api from '../services/api';
import Navbar from '../components/Navbar';
import StatsBar from '../components/StatsBar';
import PlatformCard from '../components/PlatformCard';
import GameCard from '../components/GameCard';
import './Dashboard.css';

function Dashboard() {
  const { user,logout } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [connections, setconnections] = useState([]);
  const[steamId, setSteamId] = useState('');
  const[linkLoading, setLinkLoading] = useState(false);
  const[linkError, setLinkError] = useState(null);
  const[linkSuccess, setLinkSuccess] = useState(false);



  // Xbox state
  const [xboxProfile, setXboxProfile] = useState(null);
  const [xboxNotification, setXboxNotification] = useState(null);
  const [xboxGames, setXboxGames] = useState([]);
  const allGames = [...games, ...xboxGames];
  
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

  // Load Xbox status on mount
  useEffect(() => {
    getXboxStatus()
      .then(res => setXboxProfile(res.data))
      .catch(() => setXboxProfile(null));
  }, []);

  useEffect(() => {
    console.log('xboxProfile:', xboxProfile);
    if (xboxProfile?.linked) {
        fetchXboxGames()
            .then(res => {
                console.log('Xbox games:', res.data);
                setXboxGames(res.data);
            })
            .catch(() => setXboxGames([]));
    }
}, [xboxProfile]);

// Handle ?xbox=linked or ?xbox=error from callback redirect
useEffect(() => {
  const xboxParam = searchParams.get('xbox');
  if (xboxParam === 'linked') {
    setXboxNotification({ type: 'success', message: 'Xbox account connected successfully!'});
    //Refresh Xbox status
    getXboxStatus()
      .then(res => setXboxProfile(res.data))
      .catch(() => {});
      //Clear the query param
      setSearchParams({});
  } else if (xboxParam === 'error') {
    setXboxNotification({type: 'error', message: 'Failed to connect Xbox account. Please try again.'});
    setSearchParams({});
  }
}, [searchParams]);

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

  async function handleXboxDisconnect() {
    try {
      await disconnectXbox();
      setXboxProfile(null);
      setXboxNotification({ type: 'success', message: 'Xbox account disconnected.' });
    } catch {
      setXboxNotification({ type: 'error', message: 'Failed to disconnect Xbox account.' });
    }
  }
  async function handleSteamDisconnect() {
    try {
        await api.delete('/api/platforms/unlink', { params: { platform: 'STEAM' } });
        // Refresh connections from server instead of updating locally
        const response = await api.get('/api/platforms');
        setconnections(response.data);
        setGames([]);
    } catch {
        console.error('Failed to disconnect Steam');
    }
}

  const steamConnected = connections.find(c => c.platform === 'STEAM' && c.active);

  return (
    <div className="dashboard">
      <Navbar />
      <div className="dashboard-content">

          {/* Xbox Notification */}
            {xboxNotification && (
              <div className={`dashboard-notification ${xboxNotification.type === 'error' ? 'dashboard-notification--error' : ''}`}>
                {xboxNotification.message}
                <button onClick={() => setXboxNotification(null)}>✕</button>
              </div>
          )}

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
                onSteamDisconnect={handleSteamDisconnect}
              />
              <PlatformCard platform="XBOX" 
                connected={!!xboxProfile?.linked}
                platformUserId={xboxProfile?.gamertag}
                onXboxConnect={connectXbox}
                onXboxDisconnect={handleXboxDisconnect}
               />
              <PlatformCard platform="EPIC" connected={false} />
              </div>
          </section>

         <div className="dashboard-section--main">
        {/* Games List */}
        <section className="dashboard-section">
          <div className="section-header">
            <h2 className="section-title">Your Library</h2>
            <span className="section-count">{allGames.length} games</span>
          </div>
          {loading && <p className="dashboard-message">Loading games...</p>}
          {error && <p className="dashboard-message dashboard-message--error">{error}</p>}
          {!loading && !error && allGames.length === 0 && (
            <p className="dashboard-message">No games found. Connect a platform to see your library.</p>
          )}
          <div className="games-grid">
            {allGames.map(game => (
              <GameCard key={game.id} game={game} />
            ))}
          </div>
        </section>
      </div>
      {/* Stats Bar */}
      <aside className="dashboard-section--stats">
        <StatsBar games={allGames} 
                  connections={connections}
                  xboxLinked={!!xboxProfile?.linked}
                  />
      </aside>
    </div>
    </div>
  );
}

export default Dashboard;