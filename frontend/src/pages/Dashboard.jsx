import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { fetchSteamGames } from '../services/steamService';
import api from '../services/api';

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
    <div>
    <div>
      <h1>Game Grimoire Dashboard</h1>
      <p>Welcome, {user?.username}!</p>
      <button onClick={logout}>Logout</button>
      </div>

      {/* Platform Linking */}
      <div> 
        <h2>Connected Gaming Platforms</h2>

        <div>
          <h3>Steam</h3>
          {steamConnected ? (
            <p> Connected (ID: {steamConnected.platformUserId})</p>
          ) : (
            <form onSubmit={handleLinkSteam}> 
              <input
                type="text"
                placeholder="Enter Steam ID"
                value={steamId}
                onChange={e => setSteamId(e.target.value)}
                required
              />
              <button type="submit" disabled={linkLoading}>
                {linkLoading ? 'Linking...' : 'Link Steam Account'}
              </button>
              {linkError && <p style={{ color: 'red' }}>{linkError}</p>}
              {linkSuccess && <p style={{ color: 'green' }}>Steam account linked successfully!</p>}
            </form>
          )}
        </div>
        <div>
          <h3>Xbox</h3>
          <p>Coming soon!</p>
        </div>
        <div>
          <h3>Epic Games</h3>
          <p>Coming soon!</p>
          </div>
        </div>


        {/* Games List */}
        <div>
          <h2>Your Games</h2>
          {loading && <p>Loading games...</p>}
          {error && <p style={{ color: 'red' }}>{error}</p>}
        {!loading && !error && games.length === 0 && (
          <p>No games found. Connect your gaming platforms to see your library.</p>

          )}
          <ul>
            {games.map(game => (
              <li key={game.id}>
                {game.name} - {Math.round(game.playtimeMinutes / 60)} hours played
                </li>
            ))}
          </ul>
        </div>
      </div>
  );
}

export default Dashboard;