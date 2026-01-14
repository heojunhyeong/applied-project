import React, { useState } from 'react';
import './Profile.css';

const Profile = () => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [imageUrl, setImageUrl] = useState(null);
    const [error, setError] = useState(null);
    const [userType, setUserType] = useState('users'); // 'users' 또는 'seller'

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (!file.type.startsWith('image/')) {
                setError('이미지 파일만 업로드 가능합니다.');
                return;
            }
            setSelectedFile(file);
            setError(null);
        }
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            setError('파일을 선택해주세요.');
            return;
        }

        setUploading(true);
        setError(null);

        try {
            // 토큰 체크 제거 (테스트용)
            // const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            // if (!token) {
            //     throw new Error('로그인이 필요합니다.');
            // }

            // 사용자 타입에 따라 URL 결정
            const apiUrl = userType === 'users'
                ? 'http://localhost:8080/api/users/profile/presigned-url'
                : 'http://localhost:8080/api/seller/profile/presigned-url';

            // 1. Presigned URL 요청 (토큰 없이)
            const response = await fetch(
                apiUrl,
                {
                    method: 'POST',
                    headers: {
                        // 'Authorization': `Bearer ${token}`,  // 제거
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ contentType: selectedFile.type })
                }
            );

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            const { presignedUrl, key } = data;

            // 2. Presigned URL로 이미지 업로드 (PUT 요청)
            const uploadResponse = await fetch(presignedUrl, {
                method: 'PUT',
                headers: {
                    'Content-Type': selectedFile.type
                },
                body: selectedFile
            });

            if (!uploadResponse.ok) {
                throw new Error('이미지 업로드에 실패했습니다.');
            }

            // 3. Public URL 생성
            const bucket = 'wearly-project'; // 실제 버킷 이름으로 변경
            const region = 'ap-northeast-2'; // 실제 리전으로 변경
            const publicUrl = `https://${bucket}.s3.${region}.amazonaws.com/${key}`;

            setImageUrl(publicUrl);
            setUploading(false);
        } catch (err) {
            console.error('업로드 실패:', err);
            setError(err.message || '업로드에 실패했습니다.');
            setUploading(false);
        }
    };

    return (
        <div className="profile-upload-container">
            <h2>프로필 이미지 업로드</h2>

            {/* 사용자 타입 선택 */}
            <div style={{ marginBottom: '20px' }}>
                <label style={{ marginRight: '10px' }}>사용자 타입:</label>
                <select
                    value={userType}
                    onChange={(e) => setUserType(e.target.value)}
                    style={{ padding: '5px 10px', fontSize: '16px' }}
                >
                    <option value="users">일반 사용자 (USER)</option>
                    <option value="seller">판매자 (SELLER)</option>
                </select>
                <p style={{ marginTop: '10px', fontSize: '14px', color: '#666' }}>
                    현재 선택: {userType === 'users' ? '일반 사용자' : '판매자'}
                    ({userType === 'users' ? '/api/users/profile/presigned-url' : '/api/seller/profile/presigned-url'})
                </p>
            </div>

            <div className="upload-section">
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    className="file-input"
                />

                {selectedFile && (
                    <div className="file-info">
                        <p>선택된 파일: {selectedFile.name}</p>
                        <p>타입: {selectedFile.type}</p>
                        <p>크기: {(selectedFile.size / 1024).toFixed(2)} KB</p>
                    </div>
                )}

                <button
                    onClick={handleUpload}
                    disabled={!selectedFile || uploading}
                    className="upload-button"
                >
                    {uploading ? '업로드 중...' : '업로드'}
                </button>
            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {imageUrl && (
                <div className="image-preview">
                    <h3>업로드된 이미지</h3>
                    <img src={imageUrl} alt="프로필" className="uploaded-image" />
                    <p className="image-url">URL: {imageUrl}</p>
                    <p style={{ marginTop: '10px', fontSize: '12px', color: '#666' }}>
                        저장 경로: {imageUrl.split('.amazonaws.com/')[1]}
                    </p>
                </div>
            )}
        </div>
    );
};

export default Profile;