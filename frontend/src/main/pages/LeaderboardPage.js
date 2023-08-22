import React from "react";

import { useParams } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";

import LeaderboardTable from "main/components/Leaderboard/LeaderboardTable";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

import { useBackend } from "main/utils/useBackend";
import { useCurrentUser } from "main/utils/currentUser";
import Background from "../../assets/PlayPageBackground.png";

import { Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';


export default function LeaderboardPage() {

  const { commonsId } = useParams();
  const { data: currentUser } = useCurrentUser();

  const navigate = useNavigate();

  // Stryker disable all 
  const { data: userCommons, error: _error, status: _status } =
    useBackend(
      [`/api/usercommons/commons/all?commonsId=${commonsId}`],
      {
        method: "GET",
        url: "/api/usercommons/commons/all",
        params: {
          commonsId: commonsId
        }
      },
      []
    );
  // Stryker restore all 

  // Stryker disable all 
  const { data: commons, error: _commonsError, status: _commonsStatus } =
    useBackend(
      [`/api/commons?id=${commonsId}`],
      {
        method: "GET",
        url: "/api/commons",
        params: {
          id: commonsId
        }
      },
      []
    );
  // Stryker restore all 

  const showLeaderboard = (hasRole(currentUser, "ROLE_ADMIN") || commons.showLeaderboard );
  return (
    <div data-testid={"LeaderboardPage-main-div"} style={{backgroundSize: 'cover', backgroundImage: `url(${Background})`}}>
        <BasicLayout>
            <div className="pt-2">
              <Button
                variant="primary"
                onClick={() => navigate(-1)}
                data-testid={"Leaderboard-back"}
              >
                Back
              </Button>
                <h1>Leaderboard</h1>
                {
                  showLeaderboard?
                  (<LeaderboardTable leaderboardUsers={userCommons} currentUser={currentUser} />) :
                  (<p>You're not authorized to see the leaderboard.</p>)
                }
                </div>
        </BasicLayout>
    </div>
  )
}
