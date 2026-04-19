import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
    const { user, logout } = useAuth();

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <span className="navbar-icon">📖</span>
                <div>
                    <span className="navbar-title">Game Grimoire</span>
                    <span className="navbar-sub">Your Gaming Library</span>
                </div>
            </div>
            <div className="navbar-right">
                <span className="navbar-welcome">Welcome, {user?.displayname || user?.username}!</span>
                <button className="navbar-logout" onClick={logout}>Logout</button>
            </div>
        </nav>
    );
}
