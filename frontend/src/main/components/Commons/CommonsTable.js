import React from "react";
import OurTable, {ButtonColumn} from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";
import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/commonsUtils"
import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';

export default function CommonsTable({ commons, currentUser }) {

    const navigate = useNavigate();

    const [isModalOpen, setModalOpen] = React.useState(false);
    const [itemToDelete, setItemToDelete] = React.useState(null);

    const editCallback = (cell) => {
        navigate(`/admin/editcommons/${cell.row.values["commons.id"]}`)
    }

    const deleteMutation = useBackendMutation(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/commons/allplus"]
    );

    const deleteCallback = async (cell) => { 
        setItemToDelete(cell);
        setModalOpen(true);
    }

    const confirmDelete = async () => {
        deleteMutation.mutate(itemToDelete);
        setModalOpen(false);
    }

    const leaderboardCallback = (cell) => {
        navigate(`/leaderboard/${cell.row.values["commons.id"]}`)
    }

    const columns = [
        {
            Header: 'id',
            accessor: 'commons.id', // accessor is the "key" in the data

        },
        {
            Header:'Name',
            accessor: 'commons.name',
        },
        {
            Header:'Cow Price',
            accessor: row => row.commons.cowPrice,
            id: 'commons.cowPrice'
        },
        {
            Header:'Milk Price',
            accessor: row => row.commons.milkPrice,
            id: 'commons.milkPrice'
        },
        {
            Header:'Starting Balance',
            accessor: row => row.commons.startingBalance,
            id: 'commons.startingBalance'
        },
        {
            Header:'Starting Date',
            accessor: row => String(row.commons.startingDate).slice(0,10),
            id: 'commons.startingDate'
        },
        {
            Header:'Degradation Rate',
            accessor: row => row.commons.degradationRate,
            id: 'commons.degradationRate'
        },
        {
            Header:'Show Leaderboard?',
            id: 'commons.showLeaderboard', // needed for tests
            accessor: (row, _rowIndex) => String(row.commons.showLeaderboard) // hack needed for boolean values to show up
        },
        {
            Header: 'Cows',
            accessor: 'totalCows'
        },
        {
            Header: 'Carrying Capacity',
            accessor: row => row.commons.carryingCapacity,
            id: 'commons.carryingCapacity'
        }
    ];

    const testid = "CommonsTable";

    const columnsIfAdmin = [
        ...columns,
        ButtonColumn("Edit",
"primary", editCallback, testid),
        ButtonColumn("Delete",
"danger", deleteCallback, testid),
        ButtonColumn("Leaderboard",
"secondary", leaderboardCallback, testid)
    ];

    const columnsToDisplay = hasRole(currentUser,"ROLE_ADMIN") ? columnsIfAdmin : columns;

    const deleteModal = (
        <Modal data-testid="CommonsTable-Modal" show={isModalOpen} onHide={() => setModalOpen(false)}>
            <Modal.Header closeButton>
                <Modal.Title>Delete Confirmation</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                Warning: You Are Deleting a Commons
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" data-testid="CommonsTable-Modal-Cancel" onClick={() => setModalOpen(false)}>
                    Cancel
                </Button>
                <Button variant="danger" data-testid="CommonsTable-Modal-Delete" onClick={confirmDelete}>
                    Delete
                </Button>
            </Modal.Footer>
        </Modal>
    );

    return (
        <>
            <OurTable data={commons} columns={columnsToDisplay} testid={testid} />
            {deleteModal}
        </>
    );
};
