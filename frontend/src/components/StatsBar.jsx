import'./StatsBar.css';

export default function StatsBar({ games, connections, xboxLinked}) {
    const totalGames = games?.length || 0;
    const totalHours = Math.round(
        (games?.reduce((acc, g) => acc + (g?.playtimeMinutes || 0), 0) || 0) / 60
    );
    const platformConnected = connections?.filter(c => c.active).length || 0;
    const mostplayed = games?.reduce((prev, curr) => {
        if (!prev) return curr;
        if (!curr) return prev;
        return (prev.playtimeMinutes || 0) > (curr.playtimeMinutes || 0) ? prev : curr;
    }, null);

    const stats = [
        { label: 'Total Games', value: totalGames },
        { label: 'Total Hours Played', value: totalHours.toLocaleString()},
        { label: 'Platforms Connected', value: platformConnected },
    ];

    console.log('connections:', connections);
console.log('xboxLinked:', xboxLinked);

    return (
        <div className="statsbar">
            {stats.map((stat, i) => (
                 <div className="stat-card" key={i}>
                    <div className="stat-value">{stat.value}</div>
                    <div className="stat-label">{stat.label}</div>
                </div>
            ))}
            {/* Most Played Game */}
            <div className="stat-card stat-card--mostplayed">
                {mostplayed && (
    <img
        src={mostplayed.platform === 'XBOX' 
            ? mostplayed.iconUrl 
            : `https://cdn.akamai.steamstatic.com/steam/apps/${mostplayed.platformGameId}/header.jpg`}
        alt={mostplayed.name}
        className="stat-mostplayed-img"
    />
)}
                <div className="stat-label">Most Played</div>
                <div className="stat-value stat-value--small">{mostplayed?.name || 'N/A'}      
                </div>
                <div className="stat-label">{mostplayed ? `${Math.round((mostplayed.playtimeMinutes || 0) / 60)} hrs` : ''}
                </div>
            </div>
        </div>
    );
}   