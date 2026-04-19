import'./StatsBar.css';

export default function StatsBar({ games, connections}) {
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
        { label: 'Most Played Game', value: mostplayed ? `${mostplayed.name} (${Math.round((mostplayed.playtimeMinutes || 0) / 60)} hrs)` : 'N/A' }
    ];

    return (
        <div className="stats-bar">
            {stats.map((stat, i) => (
                <div className ="stat-card" key={i}>
                    <span className="stat-icon">{stat.icon}</span>
                    <div className={`stat-value ${stat.small ? 'stat-value--small' :''}`}>
                        {stat.value}
                    </div>
                    <div className="stat-label">{stat.label}</div>
                </div>
            ))}
        </div>
    );
}   