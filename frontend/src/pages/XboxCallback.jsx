import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

export default function XboxCallback() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    useEffect(() => {
        const status = searchParams.get('xbox');
        if (status === 'linked') {
            navigate('/dashboard?xbox=linked', { replace: true });
        } else {
            navigate('/dashboard?xbox=error', { replace: true });
        }
    }, []);

    return (
        <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            minHeight: '100vh',
            background: 'var(--bg)',
            color: 'var(--muted)',
            fontFamily: 'var(--font-display)'
        }}>
            <p>Connecting Xbox account...</p>;
        </div>
    );
}