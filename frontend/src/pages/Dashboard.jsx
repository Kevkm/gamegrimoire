import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { fetchSteamGames } from '../services/steamService';

function Dashboard() {
  const { user,logout } = useAuth();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    fetchSteamGames('test@example.com', '76561199221804091')
      .then(response => {
        setGames(response.data);
        setLoading(false);
      })
        .catch(err => {
        setError('Failed to fetch games');
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading games...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h1>Game Grimoire Dashboard</h1>
      <p>Welcome, {user.name}!</p>
      <button onClick={logout}>Logout</button>
      <p>Total Games: {games.length}</p>
      <ul>
        {games.map(game => (
            <li key={game.id}>
                {game.name} - {Math.round(game.playtimeMinutes / 60)} hours played
            </li>
        ))}
      </ul>
    </div>
  );
}

export default Dashboard;