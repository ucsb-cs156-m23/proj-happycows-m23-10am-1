import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import CommonsTable from "main/components/Commons/CommonsTable"
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import commonsPlusFixtures from "fixtures/commonsPlusFixtures";
import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/commonsUtils"

// Next line uses technique from https://www.chakshunyu.com/blog/how-to-spy-on-a-named-import-in-jest/
import * as useBackendModule from "main/utils/useBackend";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate
}));

describe("UserTable tests", () => {
  const queryClient = new QueryClient();

  test("renders without crashing for empty table with user not logged in", () => {
    const currentUser = null;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });
  test("renders without crashing for empty table for ordinary user", () => {
    const currentUser = currentUserFixtures.userOnly;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("renders without crashing for empty table for admin", () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("Has the expected column headers and content for adminUser", () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );

    const expectedHeaders = ["id", "Name", "Cow Price", 'Milk Price', 'Starting Balance', 'Starting Date', 'Degradation Rate', 'Carrying Capacity', 'Cows', 'Show Leaderboard?'];
    const expectedFields = ["id", "name", "cowPrice", "milkPrice", "startingBalance", "startingDate", "degradationRate", "carryingCapacity"];
    const testId = "CommonsTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-commons.${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-commons.id`)).toHaveTextContent("1");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.id`)).toHaveTextContent("2");


    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.name`)).toHaveTextContent("Com2");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.cowPrice`)).toHaveTextContent("1");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.milkPrice`)).toHaveTextContent("2");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.degradationRate`)).toHaveTextContent("0.01");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.carryingCapacity`)).toHaveTextContent("42");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.startingBalance`)).toHaveTextContent("10");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.startingDate`)).toHaveTextContent(/^2022-11-22$/); // regex so that we have an exact match https://stackoverflow.com/a/73298371
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.showLeaderboard`)).toHaveTextContent("true");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-totalCows`)).toHaveTextContent("0");

    expect(screen.getByTestId(`${testId}-cell-row-0-col-Edit-button`)).toHaveClass("btn-primary");
    expect(screen.getByTestId(`${testId}-cell-row-0-col-Delete-button`)).toHaveClass("btn-danger");
    expect(screen.getByTestId(`${testId}-cell-row-0-col-Leaderboard-button`)).toHaveClass("btn-secondary");
  });

  const mockMutate = jest.fn();
  const mockUseBackendMutation = {
    mutate: mockMutate,
  };

  beforeEach(() => {
    jest.spyOn(useBackendModule, "useBackendMutation").mockReturnValue(mockUseBackendMutation);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test("Delete modal opens when Delete is clicked", async () => {

    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(document.body).not.toHaveClass('modal-open');
    });

    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);

    await waitFor(() => {
      expect(document.body).toHaveClass('modal-open');
    });
  });

    test("Delete commons when Delete is clicked", async () => {

      const currentUser = currentUserFixtures.adminUser;
  
  
      // https://www.chakshunyu.com/blog/how-to-spy-on-a-named-import-in-jest/
      const useBackendMutationSpy = jest.spyOn(useBackendModule, 'useBackendMutation');
  
      render(
        <QueryClientProvider client={queryClient}>
          <MemoryRouter>
            <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
          </MemoryRouter>
        </QueryClientProvider>
      );
  
      const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
      fireEvent.click(deleteButton);
      
      const doubleDeleteButton = await screen.findByTestId("CommonsTable-Modal-Delete");
      fireEvent.click(doubleDeleteButton);
  
      await waitFor(() => {
        expect(useBackendMutationSpy).toHaveBeenCalledWith(
          cellToAxiosParamsDelete,
          { onSuccess: onDeleteSuccess },
          ["/api/commons/allplus"]
        );
      });

      await waitFor(() => {
        expect(document.body).not.toHaveClass('modal-open');
      });
  });

  test("Cancel deletion when Cancel is clicked", async () => {

    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);

    const cancelButton = await screen.findByTestId('CommonsTable-Modal-Cancel');
    fireEvent.click(cancelButton);

    await waitFor(() => {
      expect(document.body).not.toHaveClass('modal-open');
    });

    expect(mockMutate).not.toHaveBeenCalled();
  });

  test("Pressing escape cancels deletion", async () => {

    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);

    expect(document.body).toHaveClass('modal-open');

    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);

    await waitFor(() => {
      expect(document.body).not.toHaveClass('modal-open');
    });

    expect(mockMutate).not.toHaveBeenCalled();
  });

});
