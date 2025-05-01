function initTransactionChart(chartId, dates, counts, isIncome) {
    const ctx = document.getElementById(chartId);
    let chartInstance = null;

    function createChart() {
        // Удаляем предыдущий график, если он существует
        if (chartInstance) {
            chartInstance.destroy();
        }

        // Определяем цвет в зависимости от isIncome
        const backgroundColor = isIncome
            ? 'rgba(48, 223, 81, 0.5)'  // Зелёный для доходов
            : 'rgba(255, 99, 132, 0.5)'; // Красный для расходов

        const borderColor = isIncome
            ? 'rgb(66, 235, 54)'  // Зелёный для доходов
            : 'rgb(255, 99, 132)'; // Красный для расходов

        chartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: dates,
                datasets: [{
                    label: 'Объем средств',
                    data: counts,
                    backgroundColor: backgroundColor,
                    borderColor: borderColor,
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1,
                            precision: 0
                        }
                    }
                },
                plugins: {
                    legend: {
                        labels: {
                            font: {
                                size: 14
                            }
                        }
                    }
                }
            }
        });
    }

    // Создаем график при загрузке
    createChart();


    // Обработчик изменения размера окна
    window.addEventListener('resize', function() {
        createChart();
    });
}
function initCategoryChart(chartId, categoryData) {
    const ctx = document.getElementById(chartId);
    let chartInstance = null;

    function createChart() {
        // Удаляем предыдущий график, если он существует
        if (chartInstance) {
            chartInstance.destroy();
        }

        // Подготовка данных
        const labels = Object.keys(categoryData);
        const data = Object.values(categoryData);

        // Цвета для категорий
        const backgroundColors = [
            'rgba(255, 99, 132, 0.7)',
            'rgba(54, 162, 235, 0.7)',
            'rgba(255, 206, 86, 0.7)',
            'rgba(75, 192, 192, 0.7)',
            'rgba(153, 102, 255, 0.7)',
            'rgba(255, 159, 64, 0.7)',
            'rgba(199, 199, 199, 0.7)',
            'rgba(166,35,35,0.7)',
            'rgba(67,124,51,0.7)',
            'rgba(255,236,0,0.7)',
            'rgba(19,64,32,0.7)',
            'rgba(89,45,45,0.53)',
            'rgba(199, 199, 199, 0.7)',
            'rgba(9,44,243,0.7)',
            'rgba(73,113,74,0.7)',
            'rgba(169,39,198,0.7)',
            'rgba(81,7,55,0.7)',
            'rgba(105,142,184,0.7)',
            'rgba(14,136,73,0.7)',
            'rgb(202,168,174)',
            'rgba(67,124,51,0.7)',
            'rgba(38,81,143,0.7)',
            'rgba(67,124,51,0.7)',
            'rgba(174,39,39,0.7)',
            'rgba(200,90,90,0.7)',
            'rgba(38,81,143,0.7)',
            'rgba(76,168,122,0.7)'
        ];

        chartInstance = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: backgroundColors,
                    borderColor: 'rgba(255, 255, 255, 0.8)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            font: {
                                size: 16,
                                family: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
                                weight: 'bold'
                            },
                            padding: 20
                        }
                    },
                    tooltip: {
                        titleFont: {
                            size: 16
                        },
                        bodyFont: {
                            size: 14
                        },
                        callbacks: {
                            label: function (context) {
                                const label = context.label || '';
                                const value = context.raw || 0;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = Math.round((value / total) * 100);
                                return `${label}: ${value} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    // Создаем график при загрузке
    createChart();

    // Обработчик изменения размера окна
    window.addEventListener('resize', function() {
        createChart();
    });

    // Обработчики для сворачивания/разворачивания фильтров
    const filterCollapse = document.getElementById('filterCollapse');
    if (filterCollapse) {
        filterCollapse.addEventListener('shown.bs.collapse', function() {
            createChart();
        });
        filterCollapse.addEventListener('hidden.bs.collapse', function() {
            createChart();
        });
    }
}

function confirmDelete(button) {
    const accountId = button.getAttribute('data-id');
    if (confirm('Вы точно хотите удалить этот кошелек?')) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/account/delete-account/${accountId}`, {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text)
                    });
                }
                // Просто перезагружаем страницу без уведомления
                location.reload();
            })
            .catch(error => {
                // Показываем только ошибки, если они возникли
                alert('Ошибка при удалении: ' + error.message);
            });
    }
}

<!-- Удаление транзакции (меняем статус на удалена -->
function deleteTransaction(button) {
    const transactionId = button.getAttribute('data-id');
    if (confirm('Вы точно хотите удалить эту операцию?')) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/account/delete-transaction/${transactionId}`, {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text)
                    });
                }
                location.reload();
            })
            .catch(error => {
                alert('Ошибка при удалении операции: ' + error.message);
            });
    }
}

<!-- Заполняем и подготавливаем форму для редактирования -->
function prepareEditForm(button) {
    // Сори за три костыля, но я 6 часов думал как это иначе сделать, так и не додумался
    const transactionId = button.getAttribute('data-id');
    const accountName = button.getAttribute('data-account-name');
    const accountBalance = button.getAttribute('data-account-balance');

    fetch(`/account/transaction/${transactionId}`)
        .then(response => response.json())
        .then(transaction => {

            // Устанавливаем action формы
            document.getElementById('editTransactionForm').action = `/account/edit-fin-transaction/${transactionId}`;


            const accountHiddenInput = document.getElementById('editBankAccountHidden');
            const accountDisplayInput = document.getElementById('editBankAccountDisplay');

            if (accountHiddenInput && accountDisplayInput) {
                accountHiddenInput.value = accountName;
                accountDisplayInput.value = `${accountName} (${accountBalance})`;
            }

            // Заполняем форму редактирования
            document.getElementById('editTransactionId').value = transactionId;
            document.getElementById('editTransactionType').value = transaction.transactionType;
            document.getElementById('editCategory').value = transaction.categoryEnum;
            document.getElementById('editOperationStatus').value = transaction.operationStatus;
            document.getElementById('editBalance').value = transaction.balance;
            document.getElementById('editCommentary').value = transaction.commentary;
            document.getElementById('editWithdrawalAccount').value = transaction.withdrawalAccount;
            document.getElementById('editSenderBank').value = transaction.senderBank;
            document.getElementById('editRecipientBank').value = transaction.recipientBank;
            document.getElementById('editRecipientBankAccount').value = transaction.recipientBankAccount;
            document.getElementById('editRecipientTelephoneNumber').value = transaction.recipientTelephoneNumber;
            document.getElementById('editRecipientTin').value = transaction.recipientTin;
        })
        .catch(error => alert('Ошибка при редактировании операции: ' + error.message));
}

function initBankStatsChart(chartId, data, title) {
    const ctx = document.getElementById(chartId);
    let chartInstance = null;

    function createChart() {
        if (chartInstance) {
            chartInstance.destroy();
        }

        const labels = Object.keys(data);
        const counts = Object.values(data);

        chartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Количество операций',
                    data: counts,
                    backgroundColor: 'rgba(54, 162, 235, 0.7)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: title,
                        font: {
                            size: 16
                        }
                    },
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0,
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    createChart();
    window.addEventListener('resize', createChart);
}

function initTransactionStatusChart(chartId, data) {
    const ctx = document.getElementById(chartId);
    let chartInstance = null;

    function createChart() {
        if (chartInstance) {
            chartInstance.destroy();
        }

        const labels = Object.keys(data);
        const counts = Object.values(data);

        chartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Количество транзакций',
                    data: counts,
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.7)', // Зеленый для выполненных
                        'rgba(255, 99, 132, 0.7)'  // Красный для удаленных
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.raw} шт`;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0,
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    createChart();
    window.addEventListener('resize', createChart);
}